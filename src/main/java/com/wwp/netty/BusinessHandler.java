package com.wwp.netty;

import com.wwp.devices.YlcDeviceMap;
import com.wwp.entity.DevCharger;
import com.wwp.entity.FeeModel;
import com.wwp.mapper.FeeModelMapper;
import com.wwp.model.Session;
import com.wwp.model.YlcDevMsg;

import com.wwp.model.YlcMsgType;
import com.wwp.model.YlcResult;
import com.wwp.service.IDevChargerService;
import com.wwp.service.IFeeModelService;
import com.wwp.service.impl.DevChargerServiceImpl;
import com.wwp.util.SpringBeanUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import io.netty.util.concurrent.*;
import java.util.Date;
import static com.wwp.model.YlcMsgType.GET_MODEL;
import static com.wwp.model.YlcMsgType.RECORD_ACK;


public class BusinessHandler extends ChannelInboundHandlerAdapter {


   private IDevChargerService devChargerService;


    private DefaultEventExecutorGroup eventExecutorGroup;
    private IFeeModelService feeModelService;

    BusinessHandler( DefaultEventExecutorGroup eventExecutorGroup)
    {
        this.eventExecutorGroup = eventExecutorGroup;
        this.devChargerService =(IDevChargerService) SpringBeanUtils.getApplicationContext().getBean(DevChargerServiceImpl.class);
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
                                s =  YlcDeviceMap.getDEVICES().get(r.getYlcDevMsg().getSerialId());
                                s.getAckFuture().setSuccess(YlcResult.OK("success"));
                                System.out.println("dodo ");
                                break;
                            case CARD_UPDATE_ACK:
                                s =  YlcDeviceMap.getDEVICES().get(r.getYlcDevMsg().getSerialId());
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
                DevCharger devCharger =  devChargerService.getDevChargerBySerialNum(inMsg.getSerialId());

                if(devCharger !=null){
                    //System.out.println("charger: " + devCharger.getDepartId());
                    devChargerService.updateTime(devCharger.getSerialNum(),new Date());

                    if(inMsg.getHeader().getMsgType() == GET_MODEL){
                        FeeModel feeModel = ((FeeModelMapper)SpringBeanUtils.getApplicationContext().getBean(FeeModelMapper.class)).getFeeModelByCode("1324");
                        if(feeModel != null){
                            System.out.println("fee0: "+feeModel.getFee0());
                            result.setResult(feeModel);
                            //businessFuture.setSuccess(new YlcResult<FeeModel>(true,feeModel,"ok"));
                        }
                    }
                    result.setSuccess(true);
                    result.setMessage("数据库操作 ok");
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
