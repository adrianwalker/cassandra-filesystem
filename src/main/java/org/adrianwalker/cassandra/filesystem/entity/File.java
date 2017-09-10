package org.adrianwalker.cassandra.filesystem.entity;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.UUID;

@Table(keyspace = "filesystem", name = "file")
public final class File {

  private UUID id;
  private String name;
  private long size;
  private long modified;
  private String group;
  private String owner;
  private boolean hidden;
  private boolean directory;

  public File() {
  }

  @PartitionKey
  @Column(name = "id")
  public UUID getId() {

    if (null == id) {
      id = UUIDs.random();
    }

    return id;
  }

  public void setId(final UUID id) {

    this.id = id;
  }

  @Column(name = "name")
  public String getName() {

    return name;
  }

  public void setName(final String name) {

    this.name = name;
  }

  @Column(name = "size")
  public long getSize() {

    return size;
  }

  public void setSize(final long size) {

    this.size = size;
  }

  @Column(name = "modified")
  public long getModified() {

    return modified;
  }

  public void setModified(final long modified) {

    this.modified = modified;
  }

  @Column(name = "group")
  public String getGroup() {

    return group;
  }

  public void setGroup(final String group) {

    this.group = group;
  }

  @Column(name = "owner")
  public String getOwner() {

    return owner;
  }

  public void setOwner(final String owner) {

    this.owner = owner;
  }

  @Column(name = "hidden")
  public boolean isHidden() {

    return hidden;
  }

  public void setHidden(final boolean hidden) {

    this.hidden = hidden;
  }

  @Column(name = "directory")
  public boolean isDirectory() {

    return directory;
  }

  public void setDirectory(final boolean directory) {

    this.directory = directory;
  }
}
