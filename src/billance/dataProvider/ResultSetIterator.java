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
package billance.dataProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Tomáš Zikmund <tommekster@gmail.com>
 */
public class ResultSetIterator implements Iterator<ResultSet>
{
    private final ResultSet resultSet;
    private boolean prepared;

    public ResultSetIterator(ResultSet resultSet)
    {
        assert resultSet != null;
        this.resultSet = resultSet;
    }

    public Stream<ResultSet> toStream()
    {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false);
    }

    @Override
    public boolean hasNext()
    {
        try
        {
            this.prepared = this.prepared ? true : this.resultSet.next();
            return this.prepared;
        }
        catch (SQLException ex)
        {
            Logger.getLogger(ResultSetIterator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public ResultSet next()
    {
        if (this.hasNext())
        {
            this.prepared = false;
            return this.resultSet;
        }
        return null;
    }

}
