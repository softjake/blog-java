package com.softjake.blog.reactive.converter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class AsyncToRxApiTest {

    private AsyncToRxApi sample;

    @Before
    public void setUp() throws Exception {
        this.sample = new AsyncToRxApi();
    }

    @After
    public void tearDown() throws Exception {
        this.sample = null;
    }

    @Test
    public void givenLoadAsyncAsFlowable_whenSubscribing_thenWaitForCompleted()
      throws InterruptedException {
        this.sample
          .getMemberListAsyncAsFlowable()
          .subscribe(member -> {
                System.out.println(member);
                Assert.assertNotEquals("Member last name is empty.",
                  "", member.getLastName());
            },
            Throwable::printStackTrace);
        Thread.sleep(2000);
    }

    @Test
    public void givenLoadAsyncAsFlowable_whenSubscribing_thenLatchOnCompleted()
      throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        this.sample
          .getMemberListAsyncAsFlowable()
          .subscribe(
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