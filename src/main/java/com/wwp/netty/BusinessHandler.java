package com.wwp.netty;

import cn.hutool.core.date.DateTime;
import com.wwp.devices.YlcDeviceMap;
import com.wwp.entity.YlcCharger;
import com.wwp.entity.YlcChargerStatus;
import com.wwp.entity.YlcFeeModel;
import com.wwp.entity.YlcOrder;
import com.wwp.mapper.YlcChargerStatusMapper;
import com.wwp.mapper.YlcFeeModelMapper;
import com.wwp.mapper.YlcOrderMapper;
import com.wwp.model.Session;
import com.wwp.model.YlcDevMsg;

import com.wwp.model.YlcMsgType;
import com.wwp.model.YlcResult;
import com.wwp.service.IYlcChargerService;
import com.wwp.service.IYlcFeeModelService;
import com.wwp.service.impl.YlcChargerServiceImpl;
import com.wwp.util.SpringBeanUtils;
import com.wwp.util.YlcStringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import io.netty.util.concurrent.*;
import java.util.Date;

import static com.wwp.model.YlcMsgType.*;


public class BusinessHandler extends ChannelInboundHandlerAdapter {


   private IYlcChargerService ylcChargerService;


    private DefaultEventExecutorGroup eventExecutorGroup;
    private IYlcFeeModelService feeModelService;

    BusinessHandler( DefaultEventExecutorGroup eventExecutorGroup)
    {
        this.eventExecutorGroup = eventExecutorGroup;
        this.ylcChargerService =(IYlcChargerService) SpringBeanUtils.getApplicationContext().getBean(YlcChargerServiceImpl.class);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {

        Promise<YlcResult> businessFuture= new DefaultPromise<YlcResult>(eventExecutorGroup.next());
        businessFuture.addListener(new FutureListener<YlcResult>() {

            @Override
            public void operationComplete(Future<YlcResult> future) throws Exception {
                Session s;
                if (future.isSuccess()) {
                    YlcMsgType typeAck=YlcMsgType.valueOf(0);
                    YlcResult r = (YlcResult) future.get();
                    System.out.println("respone: " + r.getMessage());
                    System.out.println(" " );
                    if(r.getSuccess()){
                        YlcMsgType type = r.getYlcDevMsg().getHeader().getMsgType();
                        switch(type){
                            case HEART:
                            case AUTH:
                            case MODEL_VERIFY:
                            case GET_MODEL:
                            case UP_CHARGE_REQ:
                                typeAck = YlcMsgType.valueOf(type.value()+1);
                                r.getYlcDevMsg().getHeader().setMsgType(typeAck);
                                ctx.writeAndFlush(r);
                                break;
                            case RECORD:
                                typeAck = RECORD_ACK;
                                r.getYlcDevMsg().getHeader().setMsgType(typeAck);
                                ctx.writeAndFlush(r);
                                break;
                            case REMOTE_ON_ACK:
                            case REMOTE_OFF_ACK:
                                s =  YlcDeviceMap.getDEVICES().get(r.getYlcDevMsg().getSerialNum());
                                s.getAckFuture().setSuccess(YlcResult.OK("success"));
                                System.out.println("dodo ");
                                break;
                            case CARD_UPDATE_ACK:
                                s =  YlcDeviceMap.getDEVICES().get(r.getYlcDevMsg().getSerialNum());
                                if(r.getYlcDevMsg().getSuccess()){
                                    s.getAckFuture().setSuccess(YlcResult.OK("success CARD_UPDATE_ACK"));
                                }

                                else s.getAckFuture().setSuccess(YlcResult.error("error: "+r.getYlcDevMsg().getError()));

                                break;

                            default:
                                break;

                        }
                        //YlcMsgHeader header = new YlcMsgHeader(1,1,1,YlcMsgType.HEART_ACK);
                       // msg.setHeader(header);

                    }
                    //ctx.channel().writeAndFlush(Unpooled.copiedBuffer("12345 ", CharsetUtil.UTF_8));
                }
            }
        });


        //数据库操作全部放在线程池里面操作
       eventExecutorGroup.execute(new Runnable() {
            @Override
            public void run() {
//               try{
//                   Thread.sleep(2000);
//               }
//               catch(InterruptedException e)
//                {
//                   e.printStackTrace();
//                }
                YlcResult result = (YlcResult)msg;
                YlcDevMsg inMsg = result.getYlcDevMsg();
                YlcCharger ylcCharger =  ylcChargerService.getDevChargerBySerialNum(inMsg.getSerialNum());

                if(ylcCharger !=null){
                    //System.out.println("charger: " + devCharger.getDepartId());
                    ylcChargerService.updateTime(ylcCharger.getSerialNum(),new Date());

                    if(inMsg.getHeader().getMsgType() == GET_MODEL){
                        YlcFeeModel ylcFeeModel = ((YlcFeeModelMapper)SpringBeanUtils.getApplicationContext().getBean(YlcFeeModelMapper.class)).getFeeModelByCode("1324");
                        if(ylcFeeModel != null){
                            System.out.println("fee0: "+ ylcFeeModel.getFee0());
                            result.setResult(ylcFeeModel);
                            //businessFuture.setSuccess(new YlcResult<FeeModel>(true,feeModel,"ok"));
                        }
                        result.setMessage("数据库操作 ok");
                    }

                    if(inMsg.getHeader().getMsgType() == UP_CHARGE_REQ){
                        YlcOrder ylcOrder = new YlcOrder();
                        String orderNum = YlcStringUtils.genOrderNum(result.getYlcDevMsg().getSerialNum(),result.getYlcDevMsg().getPlugNo());
                        ylcOrder.setOrderNum(orderNum);
                        ylcOrder.setSerialNum(result.getYlcDevMsg().getSerialNum());
                        ylcOrder.setPlugNo(result.getYlcDevMsg().getPlugNo());
                        ylcOrder.setPhysicalNum(result.getYlcDevMsg().getPhysicalNum());
                        ((YlcOrderMapper)SpringBeanUtils.getApplicationContext().getBean(YlcOrderMapper.class)).add(ylcOrder);

                        result.getYlcDevMsg().setOrderNum(orderNum);
                        //创建状态记录
                        YlcChargerStatus ylcChargerStatus = new YlcChargerStatus();
                        ylcChargerStatus.setOrderNum(orderNum);
                        ylcChargerStatus.setSerialNum(result.getYlcDevMsg().getSerialNum());
                        ylcChargerStatus.setPlugNo(result.getYlcDevMsg().getPlugNo());

                        ((YlcChargerStatusMapper)SpringBeanUtils.getApplicationContext().getBean(YlcChargerStatusMapper.class)).add(ylcChargerStatus);
                        result.setMessage("生成订单 ok");
                    }

                    if(inMsg.getHeader().getMsgType() ==UP_RT_STATUS)
                    {
                        YlcChargerStatus ylcChargerStatus = new YlcChargerStatus();
                        ylcChargerStatus.setOrderNum(inMsg.getOrderNum());
                        //ylcChargerStatus.setSerialNum(inMsg.getSerialNum());
                        //ylcChargerStatus.setPlugNo(result.getYlcDevMsg().getPlugNo());
                        ylcChargerStatus.setChargeCost(inMsg.getYlcStatusMsg().getChargeCost());
                        ylcChargerStatus.setChargeKwh(inMsg.getYlcStatusMsg().getChargeKwh());
                        ylcChargerStatus.setChargeMin(inMsg.getYlcStatusMsg().getTotalChargeTime());

                        ylcChargerStatus.setUpdateTime(new Date());

                        ((YlcChargerStatusMapper)SpringBeanUtils.getApplicationContext().getBean(YlcChargerStatusMapper.class)).update(ylcChargerStatus);
                    }

                    result.setSuccess(true);

                }
                else{
                    result.setSuccess(false);
                    result.setMessage("id not found");
                }
                businessFuture.setSuccess(result);

            }});



       // super.channelRead(ctx,msg);
    }

}
