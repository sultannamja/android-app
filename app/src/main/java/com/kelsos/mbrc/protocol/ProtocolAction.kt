package com.kelsos.mbrc.protocol

interface ProtocolAction {
  fun execute(message: ProtocolMessage)
}