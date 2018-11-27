
create table blocks(
        posx int not null,
        posy int not null,
        posz int not null,
        data bytea,

        PRIMARY KEY(posx,posy,posz)
);