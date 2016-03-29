package net.arhimag.curiousbike;

import java.util.HashMap;

/**
 * Created by Maxim.Statsenko on 25.03.2016.
 */
public class TrackPool
{
    private static HashMap<Integer, Track> trackPool = new HashMap<Integer, Track>();

    public static Track get ( int i )
    {
        if( trackPool.containsKey(i) )
            return trackPool.get(i);
        else
        {
            Track newTrack = new Track(i);
            trackPool.put(i, newTrack);
            return newTrack;
        }
    }
}
