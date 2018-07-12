package com.softjake.blog.reactive.scheduler;

import com.softjake.blog.reactive.model.Member;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
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

    @Before
    public void setUp() throws Exception {
        sample = new SyncSampleStream();
        if (this.memberFile == null) {
            this.memberFile = sample.getFileObject("members.json");
        }
    }

    @After
    public void tearDown() throws Exception {
        sample = null;
    }

//    void printThread(String title) {
//        System.out.println(String.format("%s thread: %s",
//          title, Thread.currentThread().getName()));
//    }
//
//    File getFileObject(String filename) {
//        ClassLoader cl = getClass().getClassLoader();
//        return new File(cl.getResource(filename).getFile());
//    }

    @Test
    public void testingFetchFileAsString() throws IOException {
        String content = sample.fetchFileAsString("members.json");
        Assert.assertNotEquals("", content);
        Assert.assertNotEquals(0, content.length());
    }


    @Test
    public void testingGetMemberListAsObservableString()
      throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(1);
        sample
          .getMemberListAsObservableString("members.json")
          .map(list -> new JsonArray(list))
          .flatMap(array -> Observable.fromIterable(array))
          .cast(JsonObject.class)
          .map(json -> json.mapTo(Member.class))
          //when
          .subscribe(member -> {    //then
                System.out.println(String.format("From %s = %s",
                  Thread.currentThread().getName(), member.toString()));
                Assert.assertNotEquals("Member last name is empty.",
                  "", member.getLastName());
            },
            Throwable::getMessage,
            () -> latch.countDown());
        latch.await();
    }


    ///////// GEMAOI = GetEachMemberAsObservableItem
    @Test
    public void testingGEMAOI_Main()
      throws IOException, InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(1);
        sample.printThread("Subscribe from getEachMemberAsObservableItemTest ");
        //when
        sample.getEachMemberAsObservableItem(memberFile)
          .subscribe(member -> {        //then
                sample.printThread(String.format(
                  "Observer for %s = thread: ", member.getEmail()));
            },
            Throwable::getMessage,
            () -> latch.countDown());
        latch.await();
    }

    ///////// GEMAOI = GetEachMemberAsObservableItem
    @Test
    public void testingGEMAOI_Io()
      throws IOException, InterruptedException {
        //given
        sample.printThread("Subscribe from getEachMemberAsObservableItemTest ");
        CountDownLatch latch = new CountDownLatch(1);
        sample.getEachMemberAsObservableItem(memberFile)
          .subscribeOn(Schedulers.io())  //---1---
          //when
          .subscribe(member -> {  //then
                sample.printThread(String.format(
                  "Observer for %s = thread: ", member.getEmail()));
            },
            Throwable::getMessage,
            () -> latch.countDown());
        latch.await();
    }

    ///////// GEMAOI = GetEachMemberAsObservableItem
    @Test
    public void testingGEMAOI_Io_Single()
      throws IOException, InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(1);
        sample.printThread("Subscribe from getEachMemberAsObservableItemTest ");

        sample.getEachMemberAsObservableItem(memberFile)
          .subscribeOn(Schedulers.io())  //---1---
          .observeOn(Schedulers.single())
          //when
          .subscribe(member -> {  //then
                sample.printThread(String.format(
                  "Observer for %s = thread: ", member.getEmail()));
            },
            Throwable::getMessage,
            () -> latch.countDown());
        latch.await();
    }


    @Test
    public void testingScheduling_Sequential()
      throws IOException, InterruptedException {
        //given...
        String[] regions = {"East", "West", "Down"};
        CountDownLatch latch = new CountDownLatch(1);
//        sample.printThread("From testingScheduling_Sequential");

        Observable<String> observable = Observable.fromArray(regions)
          .map(region -> String.format("members-%swood.json", region.toLowerCase()))
          .doOnEach(regionName -> sample.printThread(String.format("Submit region %s", regionName.getValue())))
          .observeOn(Schedulers.io())
          .map(regionName ->
            sample.fetchFileAsString(regionName))
          .subscribeOn(Schedulers.single())
          .observeOn(Schedulers.single());


        sample.printThread("From testingScheduling_Sequential subscribe");
        observable
          //when...
//          .map(list -> new JsonArray(list))
//          .flatMap(array -> Observable.fromIterable(array))
//          .cast(JsonObject.class)
//          .map(json -> json.mapTo(Member.class))
          .subscribe(list -> {       //then...
              sample.printThread(String.format("Print members..."));
              System.out.println(list);
            },
            Throwable::getMessage,
            () -> latch.countDown()
          );
        latch.await();
    }


    @Test
    public void testingScheduling_Concurrent() throws InterruptedException {
        //given
        String[] regions = {"East", "West", "Down"};
        CountDownLatch latch = new CountDownLatch(1);

        Observable<String> observable = Observable.fromArray(regions)
          .map(region -> String.format("members-%swood.json", region.toLowerCase()))
          .doOnEach(regionName -> sample.printThread(String.format("Submit region %s",
                        regionName.getValue())))
          .flatMap(regionName ->
              Observable.just(regionName)
                .subscribeOn(Schedulers.io())
//              .map(this::fetchFileAsString)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String t) throws Exception {
                        return sample.fetchFileAsString(t);
                    }
                })
          ).observeOn(Schedulers.single());

        sample.printThread("From testingScheduling_Concurrent subscribe");
        observable
          .subscribeOn(Schedulers.computation())
          .subscribe(list -> {
                sample.printThread("Print members");
                System.out.println(list);
            },
            Throwable::getMessage,
            () -> latch.countDown()
          );
        latch.await();
    }
}

