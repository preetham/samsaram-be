create table bank
(
    id   int unsigned auto_increment
        primary key,
    name varchar(50)  not null,
    logo varchar(100) not null,
    constraint index2
        unique (name)
);

create table category
(
    id   int unsigned auto_increment
        primary key,
    name varchar(50) not null,
    icon varchar(50) not null,
    constraint index2
        unique (name)
);

create table household
(
    id        int unsigned auto_increment
        primary key,
    name      varchar(100)                not null,
    image_url varchar(200)                not null,
    status    enum ('active', 'inactive') not null,
    owner     varchar(30)                 not null,
    constraint id_UNIQUE
        unique (id)
);

create index state
    on household (status);

create table household_members
(
    id           bigint unsigned auto_increment
        primary key,
    household_id int unsigned                not null,
    user_id      varchar(30)                 not null,
    status       enum ('active', 'inactive') not null
);

create index household_users
    on household_members (household_id, user_id);

create index userId
    on household_members (user_id);

create table transaction
(
    id             bigint unsigned auto_increment
        primary key,
    date           bigint unsigned          not null,
    description    varchar(200)             not null,
    amount         float unsigned           not null,
    category_id    int                      not null,
    user_id        varchar(30)              not null,
    payee          varchar(100)             not null,
    bank_id        int unsigned             not null,
    account_number bigint unsigned          not null,
    type           enum ('debit', 'credit') not null,
    constraint description_UNIQUE
        unique (description)
);

create index index2
    on transaction (user_id);

create index index3
    on transaction (user_id asc, date desc);

create index index4
    on transaction (user_id, type);

