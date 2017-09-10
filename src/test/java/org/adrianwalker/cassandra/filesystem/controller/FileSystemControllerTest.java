package org.adrianwalker.cassandra.filesystem.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.adrianwalker.cassandra.filesystem.entity.File;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public final class FileSystemControllerTest {

  private static final String HOST = "127.0.0.1";
  private static final int PORT = 9142;
  private static final String KEYSPACE = "filesystem";
  private static final String CQL = "filesystem.cql";

  private static final String FILENAME = "testfile.test";
  private static final String PARENT_DIRECTORY = "/testdir";
  private static final String PATH = PARENT_DIRECTORY + "/" + FILENAME;
  private static final String OWNER = "testowner";
  private static final String GROUP = "testgroup";
  private static final boolean NOT_DIRECTORY = false;
  private static final boolean NOT_HIDDEN = false;
  private static final int SIZE = 0;

  private static final String[] CLEAN_UP_CQL = {
    "TRUNCATE parent_path",
    "TRUNCATE path",
    "TRUNCATE file",
    "TRUNCATE chunk"
  };

  private static Session session;

  public FileSystemControllerTest() {
  }

  @BeforeClass
  public static void beforeClass() throws Exception {

    EmbeddedCassandraServerHelper.startEmbeddedCassandra();

    Cluster cluster = new Cluster.Builder()
            .addContactPoints(HOST)
            .withPort(PORT)
            .build();
    session = cluster.connect();

    CQLDataLoader dataLoader = new CQLDataLoader(session);
    dataLoader.load(new ClassPathCQLDataSet(CQL, KEYSPACE));
  }

  @AfterClass
  public static void afterClass() {

    session.close();
  }

  @After
  public void after() {

    for (String cql : CLEAN_UP_CQL) {
      session.execute(cql);
    }
  }

  @Test
  public void testSaveFile() {

    File file = newFile(FILENAME);

    FileSystemController controller = new FileSystemController(session);
    file = controller.saveFile(PATH, file);

    assertNotNull(file);
    assertNotNull(file.getId());
    assertTrue(file.getModified() > 0);
    assertTrue(file.getModified() < System.currentTimeMillis());
  }

  @Test
  public void testGetFile() {

    File file = newFile(FILENAME);

    FileSystemController controller = new FileSystemController(session);
    controller.saveFile(PATH, file);

    file = controller.getFile(PATH);

    assertNotNull(file);
    assertEquals(FILENAME, file.getName());
    assertEquals(NOT_DIRECTORY, file.isDirectory());
    assertEquals(OWNER, file.getOwner());
    assertEquals(GROUP, file.getGroup());
    assertEquals(NOT_HIDDEN, file.isHidden());
    assertEquals(SIZE, file.getSize());

    file = controller.getFile("/foobar");

    assertNull(file);
  }

  @Test
  public void testListFiles() {

    FileSystemController controller = new FileSystemController(session);

    int count = 3;

    for (int i = 0; i < count; i++) {
      controller.saveFile(PATH, newFile(FILENAME + i));
    }

    List<File> files = controller.listFiles(PARENT_DIRECTORY);
    files.sort((File f1, File f2) -> f1.getName().compareTo(f2.getName()));

    assertNotNull(files);
    assertEquals(count, files.size());

    for (int i = 0; i < count; i++) {

      File file = files.get(i);

      assertNotNull(file);
      assertEquals(FILENAME + i, file.getName());
      assertEquals(NOT_DIRECTORY, file.isDirectory());
      assertEquals(OWNER, file.getOwner());
      assertEquals(GROUP, file.getGroup());
      assertEquals(NOT_HIDDEN, file.isHidden());
      assertEquals(SIZE, file.getSize());
    }

    files = controller.listFiles("/foobar");

    assertNotNull(files);
    assertEquals(0, files.size());
  }

  @Test
  public void testDeleteFile() {

    File file = newFile(FILENAME);

    FileSystemController controller = new FileSystemController(session);
    controller.saveFile(PATH, file);
    boolean deleted = controller.deleteFile(PATH);
    file = controller.getFile(PATH);

    assertTrue(deleted);
    assertNull(file);

    deleted = controller.deleteFile("/foobar");
    file = controller.getFile("/foobar");

    assertFalse(deleted);
    assertNull(file);
  }

  @Test
  public void testMoveFile() {

    File file = newFile(FILENAME);

    FileSystemController controller = new FileSystemController(session);
    controller.saveFile(PATH, file);

    String toPath = "/todir/" + FILENAME;
    controller.moveFile(PATH, toPath);

    file = controller.getFile(PATH);
    assertNull(file);

    file = controller.getFile(toPath);
    assertNotNull(file);

    toPath = "/foobar/" + FILENAME;
    controller.moveFile(PATH, toPath);

    file = controller.getFile(toPath);
    assertNull(file);
  }

  @Test
  public void testIOStreams() throws IOException {

    File file = newFile(FILENAME);

    FileSystemController controller = new FileSystemController(session);
    controller.saveFile(PATH, file);

    int writeBytes = (2 * 1024 * 1024) + 1;

    try (OutputStream os = controller.createOutputStream(file)) {

      for (int i = 0; i < writeBytes; i++) {
        os.write((byte) 'x');
      }
    }

    file = controller.getFile(PATH);
    assertEquals(writeBytes, file.getSize());

    int bytesRead = 0;

    try (InputStream in = controller.createInputStream(file)) {

      int i;
      while ((i = in.read()) != -1) {

        assertEquals((byte) 'x', i);
        bytesRead += 1;
      }
    }

    assertEquals(writeBytes, bytesRead);
  }

  private File newFile(final String name) {

    File file = new File();

    file.setName(name);
    file.setDirectory(NOT_DIRECTORY);
    file.setOwner(OWNER);
    file.setGroup(GROUP);
    file.setHidden(NOT_HIDDEN);

    return file;
  }
}
