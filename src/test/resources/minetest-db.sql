
create table "blocks"(
        "posx" int not null,
        "posy" int not null,
        "posz" int not null,
        "data" bytea,
        "mtime" bigint not null default 0,

        PRIMARY KEY("posx","posy","posz")
);
