/*
 * Copyright 2017 Alessandro Patriarca.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.scarabya.eazypipe;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alessandro Patriarca
 */
public class EazyPipe {
    
    private final ConcurrentLinkedQueue channelIn;
    private final ConcurrentLinkedQueue channelOut;
    private final Object job;
    private final String methodName;
    
    private EazyPipe(ConcurrentLinkedQueue channelIn, Object job, String methodName, Object args)
    {
        this.channelIn = channelIn;
        this.channelOut = new ConcurrentLinkedQueue();
        this.job = job;
        this.methodName = methodName;
        runThread(args);
    }
    
    public EazyPipe()
    {
        this.job = null;
        this.methodName = null;
        this.channelIn = null;
        this.channelOut = null;
    }
    
    public final EazyPipe runChain(Object nextJob, String nextMethodName)
    {
        return new EazyPipe(channelOut, nextJob, nextMethodName, null);
    }
    
    public final EazyPipe runChain(Object nextJob, String nextMethodName, Object args)
    {
        return new EazyPipe(channelOut, nextJob, nextMethodName, args);
    }
    
    private void runThread(final Object args)
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    if(args == null)
                        job.getClass().getDeclaredMethod(methodName, ConcurrentLinkedQueue.class, ConcurrentLinkedQueue.class).invoke(job, channelIn, channelOut);
                    else
                        job.getClass().getDeclaredMethod(methodName, ConcurrentLinkedQueue.class, ConcurrentLinkedQueue.class, args.getClass()).invoke(job, channelIn, channelOut, args);
                }
                catch (NoSuchMethodException ex)
                {
                    Logger.getLogger(EazyPipe.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (SecurityException ex)
                {
                    Logger.getLogger(EazyPipe.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IllegalAccessException ex)
                {
                    Logger.getLogger(EazyPipe.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IllegalArgumentException ex)
                {
                    Logger.getLogger(EazyPipe.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (InvocationTargetException ex)
                {
                    Logger.getLogger(EazyPipe.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        thread.start();
    }
    
    public final ConcurrentLinkedQueue getOutput()
    {
        return channelOut;
    }    
}