
create table trainline(
    id serial not null,

    station varchar not null,
    line varchar not null,
    index int not null,
    owner varchar not null,

    -- block coordinates
    x int not null,
    y int not null,
    z int not null,

    -- mapblock coordinates
    posx int not null,
    posy int not null,
    posz int not null,

    mtime bigint not null

);

create index TRAINLINE_MAPBLOCK on trainline(posx, posy, posz);