#!/usr/bin/env bash

# This script builds on the excellent work by Lucas Jenß, described in his blog
# post "Integrating a submodule into the parent repository", but automates the
# entire process and cleans up a few other corner cases.
# https://x3ro.de/2013/09/01/Integrating-a-submodule-into-the-parent-repository.html

function usage(){
  echo "Usage: $0 <submodule-name> [<submodule-branch>]"
  echo "Merge a single branch of <submodule-name> into a repo, retaining file history."
  echo "If provided then <submodule-branch> will be merged, otherwise master."
  echo ""
  echo "options:"
  echo "  -h, --help                Print this message"
  echo "  -f, --force               Skip the warning message (use with caution)"
  echo "  -v, --verbose             Display verbose output"
}

function abort {
    echo "$(tput setaf 1)$1$(tput sgr0)"
    exit 1
}

function request_confirmation {
    read -p "$(tput setaf 4)$1 (y/n) $(tput sgr0)"
    [ "$REPLY" == "y" ] || abort "Aborted!"
}

function warn() {
  cat << EOF
    This script will convert your "${sub}" git submodule into
    a simple subdirectory in the parent repository while retaining all
    contents, file history and its own submodules.
    The script will:
      * delete the ${sub} submodule configuration from .gitmodules and
        .git/config and commit it.
      * rewrite the entire history of the ${sub} submodule so that all
        paths are prefixed by ${path}.
        This ensures that git log will correctly follow the original file
        history.
      * merge the submodule's tags into its parent repository and commit
        each tag merge individually.
        (only those tags are considered which are reachable from
         the tip of ${sub}/${branch})
      * merge the submodule into its parent repository and commit it.
      * reinstate any of the submodule's own submodules as part of the parent
        repository
    NOTE: This script might completely garble your repository, so PLEASE apply
    this only to a fresh clone of the repository where it does not matter if
    the repo is destroyed.  It would be wise to keep a backup clone of your
    repository, so that you can reconstitute it if need be.  You have been
    warned.  Use at your own risk.
EOF

  request_confirmation "Do you want to proceed?"
}

function git_version_lte() {
  OP_VERSION=$(printf "%03d%03d%03d%03d" $(echo "$1" | tr '.' '\n' | head -n 4))
  GIT_VERSION=$(git version)
  GIT_VERSION=$(printf "%03d%03d%03d%03d" $(echo "${GIT_VERSION#git version }" | sed -E "s/([0-9.]*).*/\1/" | tr '.' '\n' | head -n 4))
  echo -e "${GIT_VERSION}\n${OP_VERSION}" | sort | head -n1
  [ ${GIT_VERSION} -le ${OP_VERSION} ]
}

# Convert a url to an absolute url
#
# Parameters:
#  $1: The url to check
#  $2: The base url to use if $1 is a relative path
#
# Returns an absolute url
function absolute_url {
  local url=$1
  local base=$2

  if [[ $url =~ \.\. ]]; then
    echo "$base/$(basename $url)"
  else
    echo $url
  fi
}

function main() {

  if [ "${skip_warn}" != "true" ]; then
	warn
  fi

  if [ "${verbose}" == "true" ]; then
    set -x
  fi

  # Remove submodule and commit
  git config -f .gitmodules --remove-section "submodule.${sub}"
  if git config -f .git/config --get "submodule.${sub}.url"; then
    git config -f .git/config --remove-section "submodule.${sub}"
  fi
  rm -rf "${path}"
  git add -A .
  git commit -m "Remove submodule ${sub}"
  rm -rf ".git/modules/${sub}"

  # Rewrite submodule history
  local tmpdir="$(mktemp -d -t submodule-rewrite-XXXXXX)"
  git clone -b "${branch}" "${url}" "${tmpdir}"
  # Be sure to get all tags as well as we will mege them later on
  git fetch --tags
  pushd "${tmpdir}"
  local tab="$(printf '\t')"
  local filter="git ls-files -s | sed \"s:${tab}:${tab}${path}/:\" | GIT_INDEX_FILE=\${GIT_INDEX_FILE}.new git update-index --index-info && mv \${GIT_INDEX_FILE}.new \${GIT_INDEX_FILE} || true"
  git filter-branch --index-filter "${filter}" HEAD
  popd

  # Merge in rewritten submodule history
  git remote add "${sub}" "${tmpdir}"
  git fetch --tags "${sub}"

  if git_version_lte 2.8.4
  then
    # Previous to git 2.9.0 the parameter would yield an error
    ALLOW_UNRELATED_HISTORIES=""
  else
    # From git 2.9.0 this parameter is required
    ALLOW_UNRELATED_HISTORIES="--allow-unrelated-histories"
  fi

  # First merge all tags
  # We only consider tags reachable from the specified submodule's
  # branch. All other tags will be ignored.
  for submodule_tag in $(git tag --list --merged "${sub}/${branch}")
  do
    git merge -s ours -m "Merge submodule tag ${submodule_tag} for ${sub}/${branch}" ${ALLOW_UNRELATED_HISTORIES} "${submodule_tag}"
  done

  # Now merge actual files
  git merge -s ours --no-commit ${ALLOW_UNRELATED_HISTORIES} "${sub}/${branch}"

  rm -rf tmpdir

  # Add submodule content
  git clone -b "${branch}" "${url}" "${path}"

  # Transfer its own submodules to the parent
  add_submod_cmds=""
  if [ -f ${path}/.gitmodules ]; then
    sub_names=$(git config -f ${path}/.gitmodules --get-regex path | sed 's/.* \(.*\)$/\1/g' || true)

    for sub_name in ${sub_names}; do
      sub_branch=$(git config -f ${path}/.gitmodules --get "submodule.${sub_name}.branch") || true
      [ -n "${sub_branch}" ] && sub_branch="-b ${sub_branch}"
      sub_path=$(git config -f ${path}/.gitmodules --get "submodule.${sub_name}.path")
      sub_url=$(git config -f ${path}/.gitmodules --get "submodule.${sub_name}.url")

      # remove the sub-submodule (which should be empty) and cache the command to reinstate it
      rmdir ${path}/${sub_path}
      add_submod_cmds="$add_submod_cmds git submodule add ${sub_branch} --name ${sub_name} -- ${sub_url} ${path}/${sub_path} ; "
    done
  fi

  rm -rf "${path}/.git" "${path}/.gitmodules"
  git add "${path}"
  if [ -n "${add_submod_cmds}" ]; then
    bash -c "${add_submod_cmds}"
  fi

  git commit -m "Merge submodule contents for ${sub}/${branch}"
  git config -f .git/config --remove-section "remote.${sub}"

  set +x
  echo "$(tput setaf 2)Submodule merge complete. Push changes after review.$(tput sgr0)"
}

set -euo pipefail

declare verbose=false
declare skip_warn=false
while [ $# -gt 0 ]; do
    case "$1" in
        (-h|--help)
            usage
            exit 0
            ;;
        (-v|--verbose)
            verbose=true
            ;;
	(-f|--force)
	    skip_warn=true
	    ;;
        (*)
            break
            ;;
    esac
    shift
done

declare sub="${1:-}"
declare branch="${2:-master}"

if [ -z "${sub}" ]; then
  >&2 echo "Error: No submodule specified"
  usage
  exit 1
fi

shift

if [ -n "${1:-}" ]; then
  shift
fi

if [ -n "${1:-}" ]; then
  >&2 echo "Error: Unknown option: ${1:-}"
  usage
  exit 1
fi

if ! [ -d ".git" ]; then
  >&2 echo "Error: No git repository found.  Must be run from the root of a git repository"
  usage
  exit 1
fi

declare path="$(git config -f .gitmodules --get "submodule.${sub}.path")"
declare superproject_dir="$(dirname $(git config --get remote.origin.url))"
declare url=$(absolute_url $(git config -f .gitmodules --get "submodule.${sub}.url") $superproject_dir)

if [ -z "${path}" ]; then
  >&2 echo "Error: Submodule not found: ${sub}"
  usage
  exit 1
fi

if [ -z "${superproject_dir}" ]; then
  >&2 echo "Error: Could not determine the remote origin for this superproject: ${superproject_dir}"
  usage
  exit 1
fi

if ! [ -d "${path}" ]; then
  >&2 echo "Error: Submodule path not found: ${path}"
  usage
  exit 1
fi

main