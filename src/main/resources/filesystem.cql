/*
CREATE KEYSPACE filesystem
WITH replication = {
 	'class' : 'SimpleStrategy',
	'replication_factor' : 1
};
*/

CREATE TABLE filesystem.file (
  id uuid,
  name text,
  size bigint,
  modified bigint,
  group text,
  owner text,
  hidden boolean,
  directory boolean,
  PRIMARY KEY (id)
);

CREATE TABLE filesystem.chunk (
  file_id uuid,
  chunk_number int,
  content blob,
  PRIMARY KEY (file_id, chunk_number)
);

CREATE TABLE filesystem.path (
  path text,
  file_id uuid,
  PRIMARY KEY (path)
);

CREATE TABLE filesystem.parent_path (
  path text,
  file_id uuid,
  PRIMARY KEY (path, file_id)
);
