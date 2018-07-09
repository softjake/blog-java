package com.softjake.blog.reactive.converter;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.file.FileSystem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class RxToRxApiTest {

    RxToRxApi sample;

    @Before
    public void setUp() throws Exception {
        this.sample = new RxToRxApi();
    }

    @After
    public void tearDown() throws Exception {
    }

    FileSystem vfs = Vertx
      .vertx()
      .fileSystem();


    @Test
    public void testingGetMemberListAsFlowable()
      throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(1);
        sample
          .getMemberListAsFlowable("src/test/resources/members.json")
          //when
          .subscribe(
            member -> {
                //then
                System.out.println(member);
                Assert.assertNotEquals("Member last name is empty.",
                  "", member.getLastName());
            },
            Throwable::getMessage,
            () -> latch.countDown());
        latch.await();
    }

    @Test
    public void testingGetMemberArrayAsFlowable()
      throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(1);
        sample
          .getMemberArrayAsFlowable("src/test/resources/members.json")
          //when
          .subscribe(
            //then
            member -> {
                System.out.println(member);
                Assert.assertNotEquals("Member last name is empty.",
                  "", member.getLastName());
            },
            Throwable::getMessage,
            () -> latch.countDown());
        latch.await();
    }

}