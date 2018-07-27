
create table missions(
    id serial not null,

    name varchar not null,
    description varchar not null,
    time int not null,
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

create index MISSIONS_MAPBLOCK on missions(posx, posy, posz);