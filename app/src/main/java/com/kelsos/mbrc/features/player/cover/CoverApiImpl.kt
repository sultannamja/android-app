package com.kelsos.mbrc.features.player.cover

import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import io.reactivex.Single

class CoverApiImpl(
  private val apiBase: ApiBase
) : CoverApi {
  override fun getCover(): Single<String> {
    return apiBase.getItem(Protocol.NowPlayingCover, CoverPayload::class).map { payload ->
      if (payload.status == CoverPayload.SUCCESS) {
        return@map payload.cover
      } else {
        throw RuntimeException("Cover not available")
      }
    }
  }
}