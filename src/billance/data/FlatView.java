/*
 * The MIT License
 *
 * Copyright 2018 Tomáš Zikmund <tommekster@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package billance.data;

import billance.dataProvider.ResultSetField;

/**
 *
 * @author Tomáš Zikmund <tommekster@gmail.com>
 */
public class FlatView
{
    @ResultSetField
    public int id;
    
    @ResultSetField
    public int number;

    @ResultSetField
    public int waterId;

    @ResultSetField
    public int heatId;

    @ResultSetField
    public int eletricityId;

    @ResultSetField
    public int surface;

    @ResultSetField
    public int commonSurface = 0;

    public String getTM()
    {
        return "cm" + this.heatId;
    }

    public String getWM()
    {
        return "wm" + this.waterId;
    }

    public String getVT()
    {
        return "vt" + this.eletricityId;
    }

    public String getNT()
    {
        return "nt" + this.eletricityId;
    }

    public float getFlatCoef()
    {
        return (float) this.surface / ((float) this.commonSurface);
    }
    
    @Override
    public String toString(){
        return Integer.toString(this.number);
    }
}
