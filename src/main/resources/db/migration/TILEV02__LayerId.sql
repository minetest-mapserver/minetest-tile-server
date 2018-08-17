
alter table tiles drop constraint tiles_pkey;

alter table tiles add column layerid int not null default 0;

alter table tiles add primary key(x,y,z,layerid);

