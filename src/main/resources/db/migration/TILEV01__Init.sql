
create table tiles (
    x int not null,
    y int not null,
    z int not null,
    mtime bigint not null,
    tile bytea,

    PRIMARY KEY(x,y,z)
);

create index TILES_TIME on tiles(mtime);

