package com.refinitiv.ema.examples.rrtmdviewer.desktop.specified_endpoint;

import com.refinitiv.ema.access.*;
import com.refinitiv.ema.examples.rrtmdviewer.desktop.common.ApplicationSingletonContainer;
import com.refinitiv.ema.examples.rrtmdviewer.desktop.common.OMMViewerError;
import com.refinitiv.ema.examples.rrtmdviewer.desktop.common.model.DictionaryDataModel;
import com.refinitiv.eta.codec.CodecReturnCodes;

public class SpecifiedEndpointSettingsServiceImpl implements SpecifiedEndpointSettingsService {

    @Override
    public int connect(SpecifiedEndpointSettingsModel settings, OMMViewerError error) {
        error.clear();

        OmmConsumerConfig config;

        Map innerMap = EmaFactory.createMap();
        Map configMap = EmaFactory.createMap();
        ElementList elementList = EmaFactory.createElementList();
        ElementList innerElementList = EmaFactory.createElementList();

        /* Consumer group */
        elementList.add(EmaFactory.createElementEntry().ascii("DefaultConsumer", "Consumer_1" ));

        if(settings.getHost().size() == 1) {
            innerElementList.add(EmaFactory.createElementEntry().ascii("ChannelSet", "Channel_1"));
        }
        else {
            innerElementList.add(EmaFactory.createElementEntry().ascii("ChannelSet", "Channel_1, Channel_2"));
        }

        innerElementList.add(EmaFactory.createElementEntry().ascii( "Dictionary", "Dictionary_1"));
        innerElementList.add(EmaFactory.createElementEntry().intValue( "XmlTraceToStdout", 1));
        innerMap.add(EmaFactory.createMapEntry().keyAscii( "Consumer_1", MapEntry.MapAction.ADD, innerElementList));
        innerElementList.clear();

        elementList.add(EmaFactory.createElementEntry().map( "ConsumerList", innerMap ));
        innerMap.clear();
        configMap.add(EmaFactory.createMapEntry().keyAscii( "ConsumerGroup", MapEntry.MapAction.ADD, elementList ));
        elementList.clear();
        /* End setting Consumer group */

        for(int i =0; i< settings.getHost().size(); i++) {

            /* Channel group */
            if (settings.getConnectionDataModel().isEncrypted()) {
                innerElementList.add(EmaFactory.createElementEntry().ascii("ChannelType", "ChannelType::RSSL_ENCRYPTED"));
                innerElementList.add(EmaFactory.createElementEntry().ascii("EncryptedProtocolType", "EncryptedProtocolType::" + getProtocolType(settings.getConnectionDataModel().getConnectionType())));
            } else {
                innerElementList.add(EmaFactory.createElementEntry().ascii("ChannelType", "ChannelType::" + getProtocolType(settings.getConnectionDataModel().getConnectionType())));
            }

            innerElementList.add(EmaFactory.createElementEntry().ascii("WsProtocols", settings.getConnectionDataModel().getProtocolList()));

            innerElementList.add(EmaFactory.createElementEntry().intValue("GuaranteedOutputBuffers", 5000));
            innerElementList.add(EmaFactory.createElementEntry().intValue("ConnectionPingTimeout", 50000));
            innerElementList.add(EmaFactory.createElementEntry().ascii("Host", settings.getHost().get(i)));
            innerElementList.add(EmaFactory.createElementEntry().ascii("Port", settings.getPort().get(i)));

            String channelName = "Channel_" + (i+1);

            innerMap.add(EmaFactory.createMapEntry().keyAscii(channelName, MapEntry.MapAction.ADD, innerElementList));

            innerElementList.clear();
        }

        elementList.add(EmaFactory.createElementEntry().map( "ChannelList", innerMap ));
        innerMap.clear();

        configMap.add(EmaFactory.createMapEntry().keyAscii("ChannelGroup", MapEntry.MapAction.ADD, elementList ));
        elementList.clear();
        /* End setting Channel Group */

        /* Dictionary group */
        final DictionaryDataModel dictionaryData = settings.getDictionarySettings();
        if (dictionaryData.isDownloadFromNetwork()) {
            innerElementList.add(EmaFactory.createElementEntry().ascii( "DictionaryType", "DictionaryType::ChannelDictionary"));
        } else {
            innerElementList.add(EmaFactory.createElementEntry().ascii( "DictionaryType", "DictionaryType::FileDictionary"));
            innerElementList.add(EmaFactory.createElementEntry().ascii( "RdmFieldDictionaryFileName", dictionaryData.getFieldDictionaryPath()));
            innerElementList.add(EmaFactory.createElementEntry().ascii( "EnumTypeDefFileName", dictionaryData.getEnumDictionaryPath()));
        }
        innerMap.add(EmaFactory.createMapEntry().keyAscii( "Dictionary_1", MapEntry.MapAction.ADD, innerElementList));
        innerElementList.clear();

        elementList.add(EmaFactory.createElementEntry().map( "DictionaryList", innerMap ));
        innerMap.clear();

        configMap.add(EmaFactory.createMapEntry().keyAscii( "DictionaryGroup", MapEntry.MapAction.ADD, elementList ));
        elementList.clear();
        /* End setting Dictionary group */

        config = EmaFactory.createOmmConsumerConfig().config(configMap);

        if(!settings.getApplicationId().isEmpty()) {
            config.applicationId(settings.getApplicationId());
        }

        if(!settings.getPosition().isEmpty()) {
            config.position(settings.getPosition());
        }

        if(!settings.getUsername().isEmpty()) {
            config.username(settings.getUsername());
        }

        if (settings.getConnectionDataModel().isEncrypted() && settings.hasCustomEncrOptions()) {
            if (settings.getEncryptionSettings().getKeyFilePath() != null && !settings.getEncryptionSettings().getKeyFilePath().equals("")) {
                config.tunnelingKeyStoreFile(settings.getEncryptionSettings().getKeyFilePath());
                config.tunnelingSecurityProtocol("TLS");
            }
            if (settings.getEncryptionSettings().getKeyPassword() != null && !settings.getEncryptionSettings().getKeyPassword().equals("")) {
                config.tunnelingKeyStorePasswd(settings.getEncryptionSettings().getKeyPassword());
            }
        }

        try {
            OmmConsumer consumer = EmaFactory.createOmmConsumer(config);
            ApplicationSingletonContainer.addBean(OmmConsumer.class, consumer);
        } catch (Exception ex) {
            error.appendErrorText("Failed to create OMM Consumer:");
            error.appendErrorText(ex.getMessage());
            error.setFailed(true);
            return CodecReturnCodes.FAILURE;
        }

        return CodecReturnCodes.SUCCESS;
    }

    private String getProtocolType(SpecifiedEndpointConnectionTypes connType) {
        switch (connType) {
            case SOCKET:
            case ENCRYPTED_SOCKET:
                return "RSSL_SOCKET";
            case ENCRYPTED_WEBSOCKET:
            case WEBSOCKET:
                return "RSSL_WEBSOCKET";
            default:
                return null;
        }
    }

}
