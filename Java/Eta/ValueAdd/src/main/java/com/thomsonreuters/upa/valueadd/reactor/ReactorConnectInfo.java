package com.thomsonreuters.upa.valueadd.reactor;

import com.thomsonreuters.upa.transport.ConnectOptions;
import com.thomsonreuters.upa.transport.Transport;
import com.thomsonreuters.upa.transport.TransportFactory;

/**
 * Reactor connection information for use in {@link ReactorConnectOptions}.
 */
public class ReactorConnectInfo
{
    private boolean _enableSessionManagement;
    private String _location;
    private ReactorAuthTokenEventCallback _reactorAuthTokenEventCallback;
	
    ConnectOptions _connectOptions = null;
    int DEFAULT_TIMEOUT = 60;
    int _initTimeout = DEFAULT_TIMEOUT;

    ReactorConnectInfo()
    {
        _connectOptions = TransportFactory.createConnectOptions();
        _enableSessionManagement = false;
        _location = "us-east";
        _reactorAuthTokenEventCallback = null;        
    }

    /**
     * Returns the {@link ConnectOptions}, which is the ConnectOptions
     * associated with the underlying
     * {@link Transport#connect(ConnectOptions, com.thomsonreuters.upa.transport.Error)
     * Transport.connect} method. This includes information about the host or
     * network to connect to, the type of connection to use, and other transport
     * specific configuration information. This is described in more detail in
     * the UPA Developers Guide.
     * 
     * @return the {@link ConnectOptions}
     */
    public ConnectOptions connectOptions()
    {
        return _connectOptions;
    }

    /**
     * The amount of time (in seconds) to wait for the successful initialization
     * of a {@link ReactorChannel}. If initialization does not complete in time,
     * an event is dispatched to the application to indicate that the ReactorChannel
     * is down. Timeout must be greater than zero. Default is 60 seconds.
     * 
     * @param timeout the initialization timeout in seconds
     * 
     * @return {@link ReactorReturnCodes#SUCCESS} if the timeout is valid,
     *         otherwise {@link ReactorReturnCodes#PARAMETER_OUT_OF_RANGE} if
     *         the timeout is out of range
     */
    public int initTimeout(int timeout)
    {
        if (timeout < 1)
            return ReactorReturnCodes.PARAMETER_OUT_OF_RANGE;

        _initTimeout = timeout;
        return ReactorReturnCodes.SUCCESS;
    }

    /**
     * Returns the initialization timeout value.
     * 
     * @return the initialization timeout value
     */
    public int initTimeout()
    {
        return _initTimeout;
    }

    /**
     * Clears this object for reuse.
     */
    public void clear()
    {
        _connectOptions.clear();
        _enableSessionManagement = false;
        _location = "us-east";
        _reactorAuthTokenEventCallback = null;
        _initTimeout = DEFAULT_TIMEOUT;
    }

    /**
     * This method will perform a deep copy into the passed in parameter's 
     *          members from the ReactorConnectInfo calling this method.
     * 
     * @param destInfo the value getting populated with the values of the calling ReactorConnectInfo
     *  
     * @return {@link ReactorReturnCodes#SUCCESS} on success,
     *         {@link ReactorReturnCodes#FAILURE} if the destInfo is null. 
     */
    public int copy(ReactorConnectInfo destInfo)
    {
        if (destInfo == null)
            return ReactorReturnCodes.FAILURE;
        
        _connectOptions.copy(destInfo._connectOptions);
        destInfo._enableSessionManagement = _enableSessionManagement;   	
        destInfo._location = _location;
        destInfo._reactorAuthTokenEventCallback = _reactorAuthTokenEventCallback;
        
        destInfo._initTimeout = _initTimeout;
        return ReactorReturnCodes.SUCCESS;
    }
    
    /**
     * Specifies a Callback function that receives ReactorAuthTokenEvents. The token is requested 
     * by the Reactor for Consumer(disabling watchlist) and NiProvider applications to send login request and
	 * reissue with the token.
     * 
     * @param callback the auth token event callback.
     * 
     * @return {@link ReactorReturnCodes#SUCCESS} if the callback is not
     *         null, otherwise {@link ReactorReturnCodes#PARAMETER_INVALID}.
     *         
     * @see ReactorAuthTokenEventCallback
     * @see ReactorAuthTokenEvent         
     */
    public int reactorAuthTokenEventCallback(ReactorAuthTokenEventCallback callback)
    {
        if (callback == null)
            return ReactorReturnCodes.PARAMETER_INVALID;

        _reactorAuthTokenEventCallback = callback;
        return ReactorReturnCodes.SUCCESS;
    }
    
    /** A callback function for processing AuthTokenEvents received.
     * 
     * @return the reactorAuthTokenEventCallback
     * @see ReactorAuthTokenEventCallback
     * @see ReactorAuthTokenEvent
     */
    public ReactorAuthTokenEventCallback reactorAuthTokenEventCallback()
    {
        return _reactorAuthTokenEventCallback;
    }        
    
    /**
     * If set to true, enable to get access token and refresh it 
     * on behalf of users to keep session active.
     * 
     * @param enableSessionManagement enables session management
     */
    public void enableSessionManagement(boolean enableSessionManagement) 
    {
    	_enableSessionManagement = enableSessionManagement;
    }

    /**
     * If true, the channel will get access token and refresh it 
     * on behalf of users to keep session active.
     * 
     * @return the enableSessionManagement
     */
    public boolean enableSessionManagement()
    {
    	return _enableSessionManagement;
    }
    
     /**
     * Specifies the location to get a service endpoint to establish a connection with service provider.
     * Defaults to "us-east if not specified. The Reactor always uses the endpoint which provides
     * two available zones for the location.
     * 
     * @param location specifies the location endpoint
     */
    public void location(String location)
    {
    	_location = location;
    }

    /**
     * Specifies the location to get a service endpoint to establish a connection with service provider.
     * Defaults to "us-east if not specified. The Reactor always uses the endpoint which provides
     * two available zones for the location.
     * 
     * @return the location of the endpoint
     */
    public String location()
    {
    	return _location;
    }
}
