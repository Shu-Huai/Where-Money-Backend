create table if not exists budget
(
    id               int auto_increment
        primary key,
    book_id          int                         not null,
    bill_category_id int                         not null,
    used             decimal(10, 2) default 0.00 null,
    times            int            default 0    null,
    limit_amount     decimal(10, 2)              null
);

create table if not exists user
(
    id              int auto_increment
        primary key,
    user_name       varchar(255)                        not null,
    hashed_password varchar(255)                        not null,
    is_active       tinyint(1)                          not null,
    create_time     timestamp default CURRENT_TIMESTAMP not null,
    constraint user_id_uindex
        unique (id),
    constraint user_user_name_uindex
        unique (user_name)
);

create table if not exists asset
(
    id         int auto_increment
        primary key,
    user_id    int                                         not null,
    type       enum ('资金', '信用卡', '充值', '投资理财') not null,
    balance    decimal(10, 2)                              not null,
    asset_name varchar(255)                                not null,
    bill_date  int                                         null,
    repay_date int                                         null,
    quota      decimal(10, 2)                              null,
    in_total   tinyint(1)                                  not null,
    svg        varchar(255)                                null,
    constraint asset_id_uindex
        unique (id),
    constraint asset_ibfk_1
        foreign key (user_id) references user (id)
);

create index user_id
    on asset (user_id);

create table if not exists book
(
    id           int auto_increment
        primary key,
    user_id      int                                      not null,
    title        varchar(255)                             not null,
    create_time  timestamp      default CURRENT_TIMESTAMP not null,
    begin_date   int            default 1                 not null,
    total_budget decimal(10, 2)                           null,
    used_budget  decimal(10, 2) default 0.00              null,
    constraint book_id_uindex
        unique (id),
    constraint book_ibfk_1
        foreign key (user_id) references user (id)
);

create table if not exists bill_category
(
    id                 int auto_increment
        primary key,
    book_id            int                           null,
    bill_category_name varchar(255)                  not null,
    svg                text                          not null,
    type               enum ('收入', '支出', '转账') not null,
    constraint bill_category_id_uindex
        unique (id),
    constraint bill_category_ibfk_1
        foreign key (book_id) references book (id)
);

create table if not exists income_bill
(
    id                 int auto_increment
        primary key,
    book_id            int            not null,
    income_asset_id    int            not null,
    bill_category_id   int            not null,
    amount             decimal(10, 2) not null,
    bill_time          timestamp      null,
    remark             varchar(255)   null,
    image              mediumblob     null,
    image_content_type varchar(63)    null,
    constraint income_bill_id_uindex
        unique (id),
    constraint income_bill_ibfk_1
        foreign key (book_id) references book (id),
    constraint income_bill_ibfk_2
        foreign key (income_asset_id) references asset (id),
    constraint income_bill_ibfk_3
        foreign key (bill_category_id) references bill_category (id)
);

create index bill_category_id
    on income_bill (bill_category_id);

create index book_id
    on income_bill (book_id);

create index income_asset_id
    on income_bill (income_asset_id);

create table if not exists pay_bill
(
    id                 int auto_increment
        primary key,
    book_id            int                  not null,
    pay_asset_id       int                  not null,
    bill_category_id   int                  not null,
    amount             decimal(10, 2)       not null,
    bill_time          timestamp            null,
    remark             varchar(255)         null,
    refunded           tinyint(1) default 0 not null,
    image              mediumblob           null,
    image_content_type varchar(63)          null,
    constraint pay_bill_id_uindex
        unique (id),
    constraint pay_bill_ibfk_1
        foreign key (book_id) references book (id),
    constraint pay_bill_ibfk_2
        foreign key (pay_asset_id) references asset (id),
    constraint pay_bill_ibfk_3
        foreign key (bill_category_id) references bill_category (id)
)
    comment '支出账单';

create index bill_category_id
    on pay_bill (bill_category_id);

create index book_id
    on pay_bill (book_id);

create index pay_asset_id
    on pay_bill (pay_asset_id);

create table if not exists refund_bill
(
    id                 int auto_increment
        primary key,
    book_id            int            not null,
    pay_bill_id        int            not null,
    refund_asset_id    int            not null,
    amount             decimal(10, 2) not null,
    bill_time          timestamp      null,
    remark             varchar(255)   null,
    image              mediumblob     null,
    image_content_type varchar(63)    null,
    constraint refund_bill_id_uindex
        unique (id),
    constraint refund_bill_ibfk_1
        foreign key (book_id) references book (id),
    constraint refund_bill_ibfk_2
        foreign key (pay_bill_id) references pay_bill (id),
    constraint refund_bill_ibfk_3
        foreign key (refund_asset_id) references asset (id)
);

create index book_id
    on refund_bill (book_id);

create index pay_bill_id
    on refund_bill (pay_bill_id);

create index refund_asset_id
    on refund_bill (refund_asset_id);

create table if not exists transfer_bill
(
    id                 int auto_increment
        primary key,
    book_id            int            not null,
    in_asset_id        int            null,
    out_asset_id       int            null,
    amount             decimal(10, 2) not null,
    transfer_fee       decimal(10, 2) null,
    bill_time          timestamp      null,
    remark             varchar(255)   null,
    image              mediumblob     null,
    image_content_type varchar(63)    null,
    constraint transfer_bill_id_uindex
        unique (id),
    constraint transfer_bill_ibfk_1
        foreign key (book_id) references book (id),
    constraint transfer_bill_ibfk_2
        foreign key (in_asset_id) references asset (id),
    constraint transfer_bill_ibfk_3
        foreign key (out_asset_id) references asset (id)
);

create index book_id
    on transfer_bill (book_id);

create index in_asset_id
    on transfer_bill (in_asset_id);

create index out_asset_id
    on transfer_bill (out_asset_id);
