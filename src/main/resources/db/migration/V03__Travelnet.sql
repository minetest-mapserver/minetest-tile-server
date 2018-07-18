
create table travelnet(
    id serial not null,

    name varchar not null,
    network varchar not null,
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

create index TRAVELNET_MAPBLOCK on travelnet(posx, posy, posz);