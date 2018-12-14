
create table "tiles" (
    "x" int not null,
    "y" int not null,
    "z" int not null,
    "mtime" bigint not null,
    "tile" bytea,
    "layerid" int not null default 0,

    PRIMARY KEY("layerid", "x","y","z")
);


