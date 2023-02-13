package com.wwp.netty;

import com.wwp.devices.YlcDeviceMap;
import com.wwp.entity.*;
import com.wwp.mapper.YlcChargerStatusMapper;
import com.wwp.mapper.YlcFeeModelMapper;
import com.wwp.mapper.YlcOrderMapper;
import com.wwp.mapper.YlcUserOrderMapper;
import com.wwp.model.Session;
import com.wwp.model.YlcDevMsg;

import com.wwp.model.YlcMsgType;
import com.wwp.model.YlcResult;
import com.wwp.service.IYlcChargerService;
import com.wwp.service.IYlcFeeModelService;
import com.wwp.service.IYlcOrderService;
import com.wwp.service.IYlcUserLogicService;
import com.wwp.service.impl.YlcChargerServiceImpl;
import com.wwp.service.impl.YlcOrderServiceImpl;
import com.wwp.util.SpringBeanUtils;
import com.wwp.util.YlcStringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import io.netty.util.concurrent.*;

import java.math.BigDecimal;
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
                    else{
                        //TODO 记录错误
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
                YlcCharger ylcCharger =  ylcChargerService.getYlcChargerBySerialNum(inMsg.getSerialNum());

                if(ylcCharger !=null){
                    //System.out.println("charger: " + devCharger.getDepartId());
                    ylcChargerService.updateTime(ylcCharger.getSerialNum(),new Date());
                    try{
                        doBusiness(result);
                    }
                    catch (Exception e) {
                       result.setSuccess(false);
                       result.setMessage(e.getMessage());
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

    private void doBusiness( YlcResult result) throws Exception
    {
        try{
            switch (result.getYlcDevMsg().getHeader().getMsgType()){
                case GET_MODEL:
                    getModelHandle(result);break;
                case UP_CHARGE_REQ:
                    upChargeReqHandle(result);break;
                case UP_RT_STATUS:
                    upStatusHandle(result);break;
                case RECORD:
                    recordHandle(result);break;

                default:break;
            }
        }
        catch (Exception e){
            throw e;
        }

    }

    private void getModelHandle(YlcResult result)
    {

        YlcFeeModel ylcFeeModel = ((YlcFeeModelMapper)SpringBeanUtils.getApplicationContext().getBean(YlcFeeModelMapper.class)).getFeeModelByCode("1324");
        if(ylcFeeModel != null){
            System.out.println("fee0: "+ ylcFeeModel.getFee0());
            result.setResult(ylcFeeModel);
            //businessFuture.setSuccess(new YlcResult<FeeModel>(true,feeModel,"ok"));
        }
        result.setMessage("获取分段电费 ok");
    }



    private void upChargeReqHandle(YlcResult result)
    {
        YlcOrder ylcOrder = new YlcOrder();
        String orderNum = YlcStringUtils.genOrderNum(result.getYlcDevMsg().getSerialNum(),result.getYlcDevMsg().getPlugNo());
        ylcOrder.setOrderNum(orderNum);
        ylcOrder.setSerialNum(result.getYlcDevMsg().getSerialNum());
        ylcOrder.setPlugNo(result.getYlcDevMsg().getPlugNo());
        ylcOrder.setPhysicalNum(result.getYlcDevMsg().getPhysicalNum());
        ylcOrder.setSettleFlag(0);
        ((YlcOrderMapper)SpringBeanUtils.getApplicationContext().getBean(YlcOrderMapper.class)).add(ylcOrder);

        result.getYlcDevMsg().setOrderNum(orderNum);
        //创建状态记录
        YlcChargerStatus ylcChargerStatus = new YlcChargerStatus();
        ylcChargerStatus.setOrderNum(orderNum);
        ylcChargerStatus.setSerialNum(result.getYlcDevMsg().getSerialNum());
        ylcChargerStatus.setPlugNo(result.getYlcDevMsg().getPlugNo());

        ((YlcChargerStatusMapper)SpringBeanUtils.getApplicationContext().getBean(YlcChargerStatusMapper.class)).add(ylcChargerStatus);
        result.setMessage("创建卡充电记录 ok");
    }

    private void upStatusHandle(YlcResult result)
    {
        YlcChargerStatus ylcChargerStatus = new YlcChargerStatus();
        ylcChargerStatus.setOrderNum(result.getYlcDevMsg().getOrderNum());
        //ylcChargerStatus.setSerialNum(inMsg.getSerialNum());
        //ylcChargerStatus.setPlugNo(result.getYlcDevMsg().getPlugNo());
        ylcChargerStatus.setChargeCost(result.getYlcDevMsg().getYlcStatusMsg().getChargeCost());
        ylcChargerStatus.setChargeKwh(result.getYlcDevMsg().getYlcStatusMsg().getChargeKwh());
        ylcChargerStatus.setChargeMin(result.getYlcDevMsg().getYlcStatusMsg().getTotalChargeTime());

        ylcChargerStatus.setUpdateTime(new Date());

        ((YlcChargerStatusMapper)SpringBeanUtils.getApplicationContext().getBean(YlcChargerStatusMapper.class)).update(ylcChargerStatus);

        ((IYlcChargerService)SpringBeanUtils.getApplicationContext().getBean(IYlcChargerService.class)).updateStatus(result.getYlcDevMsg().getSerialNum(),
                result.getYlcDevMsg().getYlcStatusMsg().getPlugStatus(),result.getYlcDevMsg().getYlcStatusMsg().getPlugHoming(),
                result.getYlcDevMsg().getYlcStatusMsg().getSlotIn());
        result.setMessage("更新实时状态 ok");
    }

    private void recordHandle(YlcResult result)
    {
        YlcOrder ylcOrder = new YlcOrder();
        ylcOrder.setOrderNum(result.getYlcDevMsg().getOrderNum());
        ylcOrder.setSettleFlag(1);
        ylcOrder.setTotalCost(result.getYlcDevMsg().getYlcRecordMsg().getTotalCost());
        ylcOrder.setTotalKwh(result.getYlcDevMsg().getYlcRecordMsg().getRecordTotalKwh());
        ylcOrder.setStopType(result.getYlcDevMsg().getYlcRecordMsg().getStopType());

        ylcOrder.setOrderTime(result.getYlcDevMsg().getYlcRecordMsg().getBusinessDate());

        ((YlcOrderServiceImpl)SpringBeanUtils.getApplicationContext().getBean(IYlcOrderService.class)).update(ylcOrder);


        //核减金额
        if(result.getYlcDevMsg().getYlcRecordMsg().getTradeType()==1) {//app

            String userId = ((YlcUserOrderMapper)SpringBeanUtils.getApplicationContext().getBean(YlcUserOrderMapper.class)).queryUserIdByOrderNum(result.getYlcDevMsg().getOrderNum());
            if(userId!=null&&!userId.equals("")){
                BigDecimal userAmount = ((IYlcUserLogicService)SpringBeanUtils.getApplicationContext().getBean(IYlcUserLogicService.class)).queryUserAmount(userId);
                System.out.println("用户余额："+userAmount+",核减："+ylcOrder.getTotalCost()/100);
                ((IYlcUserLogicService)SpringBeanUtils.getApplicationContext().getBean(IYlcUserLogicService.class)).decreaseUserAmount(userId,new BigDecimal(ylcOrder.getTotalCost()).divide(new BigDecimal("100").setScale(0)));
            }
        }

        result.setMessage("账单结算更新 ok");
    }
}
