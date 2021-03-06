/**
 * Copyright 2009 Saptarshi Guha
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.godhuli.rhipe;


import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapreduce.Partitioner;
import org.godhuli.rhipe.REXPProtos.REXP;

public class RHPartitionerNumeric extends Partitioner<RHBytesWritable, RHBytesWritable> {

    protected static final Log LOG = LogFactory.getLog(RHPartitionerNumeric.class);

    public int getPartition(final RHBytesWritable key, final RHBytesWritable value, final int numReduceTasks) {
        int hashCode = 1;
        // taken from http://alias-i.com/lingpipe/docs/api/com/aliasi/matrix/Vector.html#hashCode%28%29
        // double hash2 = 0;
        // this is a crude and almost uses paritioning scheme.
        try {
            final REXP r = key.getParsed();
            for (int i = RHMRHelper.PARTITION_START; i <= RHMRHelper.PARTITION_END; i++) {
                final long v = Double.doubleToLongBits(r.getRealValue(i));
                final int valHash = (int) (v ^ (v >>> 32));
                hashCode = 31 * hashCode + valHash;
            }
            // hashcode = ((int)Double.doubleToLongBits(hash2));
        }
        catch (InvalidProtocolBufferException e) {
            LOG.error(e);
        }
        return (hashCode & Integer.MAX_VALUE) % numReduceTasks;
    }
    // protected int hashCode(byte[] b, int currentHash) {
    //   for (int i = 0; i < b.length; i++) {
    //     currentHash = 31*currentHash + b[i];
    //   }
    //   return currentHash;
    // }

}
