/*|-----------------------------------------------------------------------------
 *|            This source code is provided under the Apache 2.0 license      --
 *|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
 *|                See the project's LICENSE.md for details.                  --
 *|           Copyright (C) 2019-2022 Refinitiv. All rights reserved.         --
 *|-----------------------------------------------------------------------------
 */

package com.refinitiv.proxy.authentication;

public interface IProxyAuthenticatorResponse
{
    
    /**
     * Checks if is proxy connection close.
     *
     * @return true, if is proxy connection close
     */
    public boolean isProxyConnectionClose();
	
    /**
     * Gets the proxy authorization.
     *
     * @return the proxy authorization
     */
    public String getProxyAuthorization();
}
