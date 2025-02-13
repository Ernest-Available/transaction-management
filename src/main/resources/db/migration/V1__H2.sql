-- auto-generated definition
create table TRANSACTION
(
    ID          BIGINT auto_increment
        primary key,
    AMOUNT      BIGINT         not null,
    TYPE        CHARACTER VARYING(50)  not null,
    PAYER       CHARACTER VARYING(100) not null,
    PAYEE       CHARACTER VARYING(100) not null,
    DESCRIPTION CHARACTER VARYING,
    CREATE_DATE TIMESTAMP              not null,
    UPDATE_DATE TIMESTAMP
);
