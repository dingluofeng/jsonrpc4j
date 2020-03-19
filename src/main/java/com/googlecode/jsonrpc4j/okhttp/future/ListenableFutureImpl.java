/**
 * @ProjectName: 民用软件平台软件
 * @Copyright: 2012 HangZhou Hikvision System Technology Co., Ltd. All Right Reserved.
 * @address: http://www.hikvision.com
 * @date: 2017年3月4日 上午10:59:51
 * @Description: 本内容仅限于杭州海康威视数字技术股份有限公司内部使用，禁止转发.
 */
package com.googlecode.jsonrpc4j.okhttp.future;

import com.google.common.util.concurrent.AbstractFuture;

/**
 * <p></p>
 * @author DingLuoFeng 2017年3月4日 上午10:59:51
 * @version V1.0   
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2017年3月4日
 * @modify by reason:{方法名}:{原因}
 */
public class ListenableFutureImpl<V> extends AbstractFuture<V> {
    
    private V retsult;
    
    public ListenableFutureImpl() {
        super();
    }

    /* (non-Javadoc)
     * @see com.google.common.util.concurrent.AbstractFuture#set(java.lang.Object)
     */
    @Override
    public boolean set(V value) {
        retsult=value;
        return super.set(value);
    }
    
    /* (non-Javadoc)
     * @see com.google.common.util.concurrent.AbstractFuture#setException(java.lang.Throwable)
     */
    @Override
    public boolean setException(Throwable throwable) {
        return super.setException(throwable);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ListenableFutureImpl [isDone()=");
        builder.append(isDone());
        builder.append(", isCancelled()=");
        builder.append(isCancelled());
        builder.append(", retsult=");
        builder.append(retsult);
        builder.append("]");
        return builder.toString();
    }
    
}
