package billance.data;

import billance.dataProvider.ResultSetField;

public class Tariff
{
    @ResultSetField
    public float water;

    @ResultSetField
    public float heat;

    @ResultSetField
    public float fee;

    @ResultSetField
    public float elvt;

    @ResultSetField
    public float elnt;

    @ResultSetField
    public float elfee;

    @ResultSetField
    public float volumeCoef;

    @ResultSetField
    public float combustionHeat;

    @ResultSetField
    public float surfaceCoef;

    public double getVolumeCoeficient()
    {
        return volumeCoef;
    }

    public double getCombustionHeat()
    {
        return combustionHeat;
    }

    public double getMonthFee(boolean eletricity)
    {
        return fee + ((eletricity) ? elfee : 0);
    }

}
