update console_kerberos SET component_version = null;
DROP PROCEDURE IF EXISTS addKerberosVersion ;;
DELIMITER ;;
CREATE PROCEDURE addKerberosVersion ( ) BEGIN
	DECLARE type TINYINT;
	DECLARE version VARCHAR ( 25 );
	DECLARE cluster INT ( 11 );
	DECLARE lableFlag TINYINT DEFAULT FALSE;
	DECLARE components CURSOR FOR (
		SELECT
			cc.component_type_code,
			cc.hadoop_version,
			ce.cluster_id
		FROM
			console_component cc
			LEFT JOIN console_engine ce ON ce.id = cc.engine_id
			LEFT JOIN console_cluster ccl ON ccl.id = ce.cluster_id
		);
DECLARE CONTINUE HANDLER FOR NOT FOUND SET lableFlag=TRUE;
OPEN components;
label:LOOP
		FETCH components INTO type,version,cluster;
		IF lableFlag THEN
			LEAVE label;

END IF;
		IF version !=''  THEN
		if type=0 THEN

UPDATE console_kerberos ck
SET ck.component_version = version
WHERE
        ck.cluster_id = cluster
  AND ck.component_type = type;
END IF;

		IF type=1 THEN
UPDATE console_kerberos ck
SET ck.component_version = version
WHERE
        ck.cluster_id = cluster
  AND ck.component_type = type;
END IF;

END IF;
END LOOP label;
CLOSE components ;
COMMIT;
END;
CALL addKerberosVersion ( );
DROP PROCEDURE IF EXISTS addKerberosVersion;