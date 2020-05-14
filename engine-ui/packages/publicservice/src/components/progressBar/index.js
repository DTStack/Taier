import './style.scss'

class ProcessBar {
    constructor() {
        this.className = 'progress-bar'
        this.hodor = document.createElement('div')
        this.hodor.className = this.className

        this.img = document.createElement('div')
        this.img.className = 'progress-img'
        this.img.innerHTML = `<svg version="1.1" id="loader-1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"
                         width="30px" height="30px" viewBox="0 0 50 50" style="enable-background:new 0 0 50 50;" xml:space="preserve">
                        <path fill="#18a689" d="M25.251,6.461c-10.318,0-18.683,8.365-18.683,18.683h4.068c0-8.071,6.543-14.615,14.615-14.615V6.461z">
                        <animateTransform attributeType="xml"
                          attributeName="transform"
                          type="rotate"
                          from="0 25 25"
                          to="360 25 25"
                          dur="0.6s"
                          repeatCount="indefinite"/>
                        </path>
                      </svg>`
    }

    show() {
        document.body.appendChild(this.hodor)
        document.body.appendChild(this.img)
    }

    hide() {
        if (this.hasAdded()) {
            document.body.removeChild(this.hodor)
            document.body.removeChild(this.img)
        }
    }

    hasAdded() {
        return document.getElementsByClassName(this.className).length > 0
    }
}
export default new ProcessBar()
