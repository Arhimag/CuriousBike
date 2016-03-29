package net.arhimag.curiousbike;

/**
 * Created by Maxim.Statsenko on 25.03.2016.
 */
public class TourTag extends LocationTag
{
    private int tourTrackIDs[];
    private int lastPlayedTrack = -1;

    public TourTag()
    {
        super();
    }

    public TourTag(double centerLatitude, double centerLongitude, double radius)
    {
        super(centerLatitude, centerLongitude, radius);
    }

    public TourTag(double centerLatitude, double centerLongitude, double radius, double leaveCircleSecondsLeft)
    {
        super( centerLatitude, centerLongitude, radius, leaveCircleSecondsLeft );
    }

    public TourTag(double centerLatitude, double centerLongitude, double radius, double leaveCircleSecondsLeft,
                       boolean speedRelevant, double speedDirectionInaccuracy, double speedX, double speedY)
    {
        super( centerLatitude, centerLongitude, radius, leaveCircleSecondsLeft, speedRelevant, speedDirectionInaccuracy, speedX, speedY );
    }

    public TourTag(int[] tracks)
    {
        super();
        setTracks(tracks);
    }

    public TourTag(double centerLatitude, double centerLongitude, double radius, int[] tracks)
    {
        super(centerLatitude, centerLongitude, radius);
        setTracks(tracks);
    }

    public TourTag(double centerLatitude, double centerLongitude, double radius, double leaveCircleSecondsLeft, int[] tracks)
    {
        super( centerLatitude, centerLongitude, radius, leaveCircleSecondsLeft );
        setTracks(tracks);
    }

    public TourTag(double centerLatitude, double centerLongitude, double radius, double leaveCircleSecondsLeft,
                   boolean speedRelevant, double speedDirectionInaccuracy, double speedX, double speedY, int[] tracks)
    {
        super( centerLatitude, centerLongitude, radius, leaveCircleSecondsLeft, speedRelevant, speedDirectionInaccuracy, speedX, speedY );
        setTracks(tracks);
    }

    public void setTracks( int[] tracks )
    {
        tourTrackIDs = tracks.clone();
    }

    public boolean isTagReady()
    {
        return getFirstBadTrack() == null;
    }

    public Integer getFirstBadTrack()
    {
        for( int i = 0; i < tourTrackIDs.length; i++ )
            if( ! TrackPool.get(tourTrackIDs[i]).isDownloaded() )
                return tourTrackIDs[i];
        return null;
    }

    public Integer nextTrackID()
    {
        lastPlayedTrack++;
        if( lastPlayedTrack >= tourTrackIDs.length )
            return null;
        else
            return tourTrackIDs[lastPlayedTrack+1];
    }

    public void restart()
    {
        lastPlayedTrack = -1;
    }
}
