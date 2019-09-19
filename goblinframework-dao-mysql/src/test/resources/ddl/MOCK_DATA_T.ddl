CREATE TABLE MOCK_DATA_T
(
    ID       BIGINT(20)   NOT NULL AUTO_INCREMENT,
    NAME     VARCHAR(255) NOT NULL DEFAULT '',
    CT1      DATETIME     NOT NULL,
    CT2      DATETIME     NOT NULL,
    UT1      DATETIME     NOT NULL,
    UT2      BIGINT(20)   NOT NULL,
    UT3      VARCHAR(40)  NOT NULL,
    F1       VARCHAR(255) NOT NULL DEFAULT '',
    F2       VARCHAR(255) NOT NULL DEFAULT '',
    F3       VARCHAR(255) NOT NULL DEFAULT '',
    REVISION INT(11)      NOT NULL,
    PRIMARY KEY (ID)
)
    ENGINE = InnoDB;

