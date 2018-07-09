package com.softjake.blog.reactive.scheduler;

import com.softjake.blog.reactive.model.Member;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class SyncSampleStreamTest {

    private SyncSampleStream sample;
    private File memberFile = null;

    public static void setMemberFile() {
    }

    @Before
    public void setUp() throws Exception {
        if (this.memberFile == null) {
            ClassLoader cl = getClass().getClassLoader();
            this.memberFile = new File(cl.getResource("members.json").getFile());
        }
        sample = new SyncSampleStream();
    }

    @After
    public void tearDown() throws Exception {
        sample = null;
    }


    @Test
    public void testGetMemberListAsObservableString() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        sample
          .getMemberListAsObservableString(memberFile)
          .map(list -> new JsonArray(list))
          .flatMap(array -> Observable.fromIterable(array))
          .cast(JsonObject .class)
          .map(json -> json.mapTo(Member .class))
          .subscribe(member -> {
                System.out.println(String.format("From %s = %s",
                  Thread.currentThread().getName(), member.toString()));
                Assert.assertNotEquals("Member last name is empty.",
                  "", member.getLastName());
            },
            Throwable::getMessage,
            () -> latch.countDown());
        latch.await();
    }



    void printThread(String title) {
        System.out.println(String.format("%s thread: %s\n",
          title, Thread.currentThread().getName()));
    }


    @Test
    public void givenSubscribeOnMain_whenNoChange_thenResult()
      throws IOException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        printThread("Subscribe thread = ");

        sample.getEachMemberAsObservableItem(memberFile)
          .subscribe(member -> {
              printThread(String.format(
                "Observer for %s = thread: ", member.getEmail()));
            },
            Throwable::getMessage,
            () -> latch.countDown());
        latch.await();
    }

    @Test
    public void givenSubscribeOnIo_whenNoChange_thenResult()
      throws IOException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        printThread("Subscribe");

        sample.getEachMemberAsObservableItem(memberFile)
          .subscribeOn(Schedulers.io())  //---1---
          .subscribe(member -> {
                printThread(String.format(
                  "Observer for %s = thread: ", member.getEmail()));
            },
            Throwable::getMessage,
            () -> latch.countDown());
        latch.await();
    }

    @Test
    public void givenSubscribeOnIo_whenChangeToSingle_thenResult()
      throws IOException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        printThread("Subscribe");

        sample.getEachMemberAsObservableItem(memberFile)
          .subscribeOn(Schedulers.io())  //---1---
          .observeOn(Schedulers.single())
          .subscribe(member -> {
                printThread(String.format(
                  "Observer for %s = thread: ", member.getEmail()));
            },
            Throwable::getMessage,
            () -> latch.countDown());
        latch.await();
    }


}