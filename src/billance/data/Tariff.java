package billance.data;

public class Tariff
{
    public float water;
    public float heat;
    public float fee;
    public float elvt;
    public float elnt;
    public float elfee;
    public float volumeCoef;
    public float combustionHeat;
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
