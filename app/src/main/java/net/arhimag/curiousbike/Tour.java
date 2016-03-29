package net.arhimag.curiousbike;

import android.location.Location;

/**
 * Created by Maxim.Statsenko on 25.03.2016.
 */
public class Tour
{
    public static int TOUR_IS_EMPTY = -1;
    public static int TOUR_IS_NOT_LOADED = -2;
    public static int TOUR_TRACKS_IS_NOT_DOWNLOADED = -3;
    public static int TOUR_IS_OK = 0;

    private TourTag tourTags[];
    private boolean playedTourTags[];
    private int lastLoadedTag = -1;

    public Tour()
    {

    }

    public void setTourTagsLength( int tourTagsLength )
    {
        tourTags = new TourTag[tourTagsLength];
        playedTourTags = new boolean[tourTagsLength];
        lastLoadedTag = -1;
    }


    public boolean pushTourTag( TourTag newTag )
    {
        if( lastLoadedTag + 1 < tourTags.length && lastLoadedTag + 1 < playedTourTags.length )
        {
            tourTags[lastLoadedTag++] = newTag;
            playedTourTags[lastLoadedTag] = false;
            return true;
        }
        else
            return false;
    }

    public Integer getTourTagNum( Location location )
    {
        for( int i = 0; i < tourTags.length; i++ )
            if( tourTags[i] == null )
                return -1;
            else if( tourTags[i].checkLocation(location.getLatitude(), location.getLongitude()) )
                return i;
        return null;
    }

    public int getTourStatus ( )
    {
        if( tourTags == null)
            return TOUR_IS_EMPTY;
        if( tourTags.length == 0 )
            return TOUR_IS_EMPTY;
        for( int i = 0; i < tourTags.length;  i++ )
            if( tourTags[i] == null )
                return TOUR_IS_NOT_LOADED;

        for( int i = 0; i < tourTags.length;  i++ )
            if( !tourTags[i].isTagReady() )
                return TOUR_TRACKS_IS_NOT_DOWNLOADED;

        return TOUR_IS_OK;
    }

    public Integer getFirstBadTrack()
    {
        if( getTourStatus() == TOUR_TRACKS_IS_NOT_DOWNLOADED )
            for( int i = 0; i < tourTags.length; i++ )
                if( tourTags[i].getFirstBadTrack() != null )
                    return tourTags[i].getFirstBadTrack();
        return null;
    }

    public void restart()
    {
        for( int i = 0; i < playedTourTags.length; i++)
            playedTourTags[i] = false;
        for( int i = 0; i < tourTags.length; i++ )
            tourTags[i].restart();
    }

    public Integer getTrackID(Location location)
    {
        Integer result = null;
        for( int i = 0; i < tourTags.length; i++ )
            if( tourTags[i] == null )
                return null;
            else if(!playedTourTags[i] && tourTags[i].checkLocation(location.getLatitude(), location.getLongitude()) )
            {
                result = tourTags[i].nextTrackID();
                if( result != null )
                    return result;
                else
                    playedTourTags[i] = true;
            }
        return result;
    }
}
