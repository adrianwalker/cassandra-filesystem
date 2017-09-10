package org.adrianwalker.cassandra.filesystem.entity;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.UUID;

@Table(keyspace = "filesystem", name = "path")
public final class Path {

  private String path;
  private UUID fileId;

  public Path() {
  }

  public Path(final String path, final UUID fileId) {

    this.path = path;
    this.fileId = fileId;
  }

  @PartitionKey
  @Column(name = "path")
  public String getPath() {

    return path;
  }

  public void setPath(final String path) {

    this.path = path;
  }

  @Column(name = "file_id")
  public UUID getFileId() {

    return fileId;
  }

  public void setFileId(final UUID fileId) {

    this.fileId = fileId;
  }
}
