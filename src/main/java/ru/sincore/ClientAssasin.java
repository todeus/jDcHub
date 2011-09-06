/*
 * ClientAssasin.java
 *
 * Created on 26 mai 2007, 19:18
 *
 * DSHub ADC HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ru.sincore;

import ru.sincore.conf.Vars;

/**
 * Permanent thread that keeps clients connected ( meaning killing the ones who
 * are disconnected). Also sends delayed searches and will be used for cron-like jobs.
 *
 * @author Pietricica
 */
public class ClientAssasin extends Thread
{

    /**
     * Creates a new instance of ClientAssasin
     */


    public ClientAssasin()
    {

        start();
    }


    @Override
    public void run()
    {

        while (!Main.Server.restart)
        {

            if (SessionManager.Users.isEmpty())
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException ex)
                {

                }
                continue;
            }

            for (Client client : SessionManager.getUsers())
            {

                long currentTime = System.currentTimeMillis();

                /*  synchronized (temp.handler.cur_inf)
                {
                if (((temp.handler.userok==1)
                        && (temp.handler.cur_inf!=null))
                        && (curtime-temp.handler.LastINF>(1000*120L)))
                {
                    Broadcast.getInstance().broadcast(temp.handler.cur_inf);
                    temp.handler.LastINF=curtime;
                    temp.handler.cur_inf=null;
        }
                }
                */

                if (((client.handler.kicked != 1)
                     && (client.handler.InQueueSearch != null))
                    && (client.handler.userok == 1))
                {

                    double xy = 1;
                    for (int i = 0; i < client.handler.search_step; i++)
                    {
                        xy *= ((double) Vars.search_log_base) / 1000;
                    }
                    xy *= 1000;
                    long xx = (long) xy;
                    if (client.handler.search_step >= Vars.search_steps)
                    {
                        xx = Vars.search_spam_reset * 1000;
                    }
                    // System.out.println(xx);
                    if ((currentTime - client.handler.Lastsearch) > xx)
                    {

                        if (client.handler.InQueueSearch.startsWith("B"))
                        {
                            Broadcast.getInstance().broadcast(client.handler.InQueueSearch);
                        }
                        else
                        {
                            Broadcast.getInstance()
                                     .broadcast(client.handler.InQueueSearch, Broadcast.STATE_ACTIVE);
                        }
                        client.handler.InQueueSearch = null;
                        client.handler.Lastsearch = currentTime;
                    }

                }


            }
            //temp.PS.printf
            // new Broadcast("");
            //System.out.println("gay.");
            //if(temp!=null)
            // new Broadcast("IMSG Debug Message Please Ignore, Check "+temp.NI);
            try
            {
                this.sleep(5000);
            }
            catch (Exception e)
            {
            }

        }
    }

}
