package net.arhimag.curiousbike;

/**
 * Created by arhimag on 29.01.16.
 */
public class LocationTag
{
    /**
     * Глобальная переменная описывающая доступность скорости в GPS координатах,
     * как следствие при оценки на принадлежность в случае отсутствия скорости будут
     * не учитываться ограничения на скорость и на время покидание круга.
     */
    private static boolean IS_SPEED_AVALIBLE;

    /**
     * Мы делаем сильное допущение. Мы считаем, что в широту и долготу коэффициент
     * изменения соотношения координат GPS и метров один и тот же. Это дает нам
     * элептические измерения, но пока так, а потом поправим.
     */
    private static double GPS_TO_METER_CONVERTER_MOSCOW = 360 / (  40075017 * 0.85 );

    /**
     * Долгота центра круга
     */
    private double centerLatitude;
    /**
     * Широта центра круга
     */
    private double centerLongitude;
    /**
     * Радиус круга. Отрицательный радиус говорит о круге охватывающем всю плоскость.
     */
    private double radius;

    /**
     * Является ли координата зависимой от скорости
     */
    private boolean speedRelevant;
    /**
     * X координата вектора скорости движения (м/с)
     */
    private double speedX;
    /**
     * Y координата вектора скорости движения (м/с)
     */
    private double speedY;
    /**
     * Погрешность в радианах по направлению движения
     */
    private double speedDirectionInaccuracy;
    /**
     * Нормированный вектор скорости. Х - координата  (м/с)
     */
    private double normalSpeedX;
    /**
     * Нормированный вектор скорости. Y - координата  (м/с)
     */
    private double normalSpeedY;
    /**
     * Значение скорости. (м/с)
     */
    private double speedValue;
    /**
     * Не менее скольки секунд должно остаться до покидания круга.
     */
    private double leaveCircleSecondsLeft;

    /**
     * Установка глобальной переменной о применимости скоростных ограничений.
     *
     * @param avalible Новое значение глобальной переменной
     */
    public static void setIsSpeedAvalible(boolean avalible)
    {
        IS_SPEED_AVALIBLE = avalible;
    }

    /**
     * Получение значения глобальной переменно о применимости скоростных ограничений.
     *
     * @return значение глобальной переменной.
     */
    public static boolean isSpeedAvalible()
    {
        return IS_SPEED_AVALIBLE;
    }

    /**
     * Пересчет нормированного вектора.
     */
    private void recalcNormalSpeed()
    {
        speedValue = Math.sqrt(speedX * speedX + speedY * speedY);
        normalSpeedX = speedX / speedValue;
        normalSpeedY = speedY / speedValue;
    }

    /**
     * Конструктор по умолчанию не учитывает скорость и создает всеобъемлющий круг с центром в 0
     */
    public LocationTag()
    {
        centerLatitude = 0.0;
        centerLongitude = 0.0;
        radius = -1;

        speedRelevant = false;
        speedDirectionInaccuracy = Math.PI;
        speedX = 0.0;
        speedY = 0.0;

        leaveCircleSecondsLeft = 0;

        recalcNormalSpeed();
    }

    /**
     * Конструктор создающий круг без ограничений по скорости.
     */
    public LocationTag(double centerLatitude, double centerLongitude, double radius)
    {
        this();

        this.centerLatitude = centerLatitude;
        this.centerLongitude = centerLongitude;
        this.radius = radius;
    }

    /**
     * Конструктор создающий круг без ограничений по скорости, но с ограничением по времени
     */
    public LocationTag(double centerLatitude, double centerLongitude, double radius, double leaveCircleSecondsLeft)
    {
        this(centerLatitude, centerLongitude, radius);

        this.leaveCircleSecondsLeft = leaveCircleSecondsLeft;
    }


    /**
     * Конструктор полноценного ограничения
     */
    public LocationTag(double centerLatitude, double centerLongitude, double radius, double leaveCircleSecondsLeft,
                       boolean speedRelevant, double speedDirectionInaccuracy, double speedX, double speedY)
    {
        this(centerLatitude, centerLongitude, radius, leaveCircleSecondsLeft);

        this.speedRelevant = speedRelevant;
        this.speedDirectionInaccuracy = speedDirectionInaccuracy;
        this.speedX = speedX;
        this.speedY = speedY;

        recalcNormalSpeed();
    }

    public double getCenterLatitude()
    {
        return centerLatitude;
    }

    public void setCenterLatitude(double centerLatitude)
    {
        this.centerLatitude = centerLatitude;
    }

    public double getCenterLongitude()
    {
        return centerLongitude;
    }

    public void setCenterLongitude(double centerLongitude)
    {
        this.centerLongitude = centerLongitude;
    }

    public double getRadius()
    {
        return radius;
    }

    public void setRadius(double radius)
    {
        this.radius = radius;
    }

    public double getSpeedX()
    {
        return speedX;
    }

    public void setSpeedX(double speedX)
    {
        this.speedX = speedX;
        recalcNormalSpeed();
    }

    public double getSpeedY()
    {
        return speedY;
    }

    public void setSpeedY(double speedY)
    {
        this.speedY = speedY;
        recalcNormalSpeed();
    }

    public double getSpeedDirectionInaccuracy()
    {
        return speedDirectionInaccuracy;
    }

    public void setSpeedDirectionInaccuracy(double speedDirectionInaccuracy)
    {
        this.speedDirectionInaccuracy = speedDirectionInaccuracy;
    }

    public double getLeaveCircleSecondsLeft()
    {
        return leaveCircleSecondsLeft;
    }

    public void setLeaveCircleSecondsLeft(double leaveCircleSecondsLeft)
    {
        this.leaveCircleSecondsLeft = leaveCircleSecondsLeft;
    }

    public boolean isSpeedRelevant()
    {
        return speedRelevant;
    }

    public void setSpeedRelevant(boolean speedRelevant)
    {
        this.speedRelevant = speedRelevant;
    }

    public double getSpeedValue()
    {
        return speedValue;
    }

    public double getNormalSpeedX()
    {
        return normalSpeedX;
    }

    public double getNormalSpeedY()
    {
        return normalSpeedY;
    }

    public boolean useSpeed()
    {
        return IS_SPEED_AVALIBLE && speedRelevant;
    }

    public boolean checkLocation( double currentLatitude, double currentLongitude, double currentSpeedX, double currentSpeedY )
    {
        if( ! checkLocation(  currentLatitude,  currentLongitude  )  )
            return false;

        if( useSpeed() )
        {
            double currentSpeedValue = Math.sqrt( currentSpeedX * currentSpeedX + currentSpeedY * currentSpeedY );
            double currentNormalSpeedX = currentSpeedX / currentSpeedValue;
            double currentNormalSpeedY = currentSpeedY / currentSpeedValue;

            if( speedDirectionInaccuracy > 0
                && ( Math.acos( (currentNormalSpeedX * normalSpeedX + currentNormalSpeedY * normalSpeedY) / ( currentSpeedValue * speedValue ) ) > speedDirectionInaccuracy ) )
                return false;

            if( leaveCircleSecondsLeft > 0 && radius > 0 )
            {
                double c = ( currentLatitude - centerLatitude ) * ( currentLatitude - centerLatitude )
                        + ( currentLongitude - centerLongitude ) * ( currentLongitude - currentLongitude )
                        - radius * radius;

                double b = 2 * ( ( currentNormalSpeedX / GPS_TO_METER_CONVERTER_MOSCOW  ) * ( currentLongitude - centerLongitude ) +
                        + ( currentNormalSpeedY / GPS_TO_METER_CONVERTER_MOSCOW  ) * ( currentLatitude - centerLatitude ) );

                double a = ( currentNormalSpeedX * currentNormalSpeedX + currentNormalSpeedY * currentNormalSpeedY ) / ( GPS_TO_METER_CONVERTER_MOSCOW * GPS_TO_METER_CONVERTER_MOSCOW );

                double d = b*b - 4*a*c;

                // Окружность не пересекается с прямой, хотя дойти до сюда должно было только когда точка внутри окружности
                if ( d <= 0 )
                    return false;

                d = Math.sqrt( d );

                if ( Math.max( (-b + d)/ ( 2 * a ), (-b - d)/ ( 2 * a ) ) > leaveCircleSecondsLeft )
                    return false;
            }
        }

        return true;
    }

    public boolean checkLocation( double currentLatitude, double currentLongitude  )
    {
        if( ( radius > 0 ) &&
                ( ( currentLatitude - centerLatitude ) * ( currentLatitude - centerLatitude )  +
                        ( currentLongitude - centerLongitude ) * ( currentLongitude - centerLongitude ) > radius * radius ) )
            return false;
        return true;
    }
}
