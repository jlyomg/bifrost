//package com.dataour.bifrost.mq.consumer;
//
//import com.data.operation.delivery.api.RemoteDeliveryService;
//import com.data.operation.delivery.api.module.DeExportApiCallRecordDto;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.function.Consumer;
//
///**
// * 针对 {@link DeExportApiCallRecordDto} 的消费者
// *
// * @Author stl
// * @Date 2023年03月02日 10:57
// */
//@Component
//@Slf4j
//public class CallRecordConsumer implements Consumer<DeExportApiCallRecordDto> {
//
//    @Resource
//    private RemoteDeliveryService remoteDeliveryService;
//
//    @Override
//    public void accept(DeExportApiCallRecordDto deExportApiCallRecordDto) {
//        System.out.println("接收到消息：" + deExportApiCallRecordDto);
//        //保存调用记录（异步）
//        remoteDeliveryService.insertCallRecord(deExportApiCallRecordDto);
//    }
//
//}
