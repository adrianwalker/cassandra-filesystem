package org.adrianwalker.cassandra.filesystem.entity;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.nio.ByteBuffer;
import java.util.UUID;

@Table(keyspace = "filesystem", name = "chunk")
public final class Chunk {

  private UUID fileId;
  private int chunkNumber;
  private ByteBuffer content;

  public Chunk() {
  }

  @PartitionKey
  @Column(name = "file_id")
  public UUID getFileId() {

    return fileId;
  }

  public void setFileId(final UUID fileId) {

    this.fileId = fileId;
  }

  @ClusteringColumn
  @Column(name = "chunk_number")
  public int getChunkNumber() {

    return chunkNumber;
  }

  public void setChunkNumber(final int chunkNumber) {

    this.chunkNumber = chunkNumber;
  }

  @Column(name = "content")
  public ByteBuffer getContent() {

    return content;
  }

  public void setContent(final ByteBuffer content) {

    this.content = content;
  }
}
