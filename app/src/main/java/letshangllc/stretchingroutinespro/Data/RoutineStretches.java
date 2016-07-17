package letshangllc.stretchingroutinespro.Data;

import letshangllc.stretchingroutinespro.JavaObjects.Stretch;

/**
 * Created by cvburnha on 4/14/2016.
 */
public final class RoutineStretches {
    public static Stretch[] morningStretches = {Stretches.forwardBend, Stretches.upperBackRelease,
            Stretches.chairPose,
            Stretches.sideReachStretchLeft, Stretches.sideReachStretchRight, Stretches.downWardDog, Stretches.plank
    };

    public static Stretch[] backStretches = {Stretches.sitAndReach, Stretches.kneePress, Stretches.hamStringStretchLeft,
            Stretches.hamStringStretchRight,Stretches.cobraPose,
            Stretches.harePose, Stretches.cowPose, Stretches.catPose,
    };

    public static Stretch[] fullBodyStretches = {Stretches.chairPose, Stretches.simplePeacock, Stretches.warriorPoseLungeLeft,
        Stretches.warriorPoseLungeRight, Stretches.camelPose, Stretches.upwardTablePose,
            Stretches.plank, Stretches.boatStrech
    };

    public static Stretch[] legStretches = {Stretches.hamStringStretchLeft, Stretches.hamStringStretchRight,
     Stretches.butterfly, Stretches.downWardDog, Stretches.warriorPoseLungeLeft, Stretches.warriorPoseLungeRight};

    public static Stretch[] beforeBedStretches= {Stretches.forwardBend, Stretches.downWardDog,
        Stretches.lizardPoseLeft, Stretches.lizardPoseRight, Stretches.sitAndReach, Stretches.happyBaby
    };
}
