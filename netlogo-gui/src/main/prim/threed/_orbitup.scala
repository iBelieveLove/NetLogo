// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.prim.threed

import org.nlogo.core.Syntax
import org.nlogo.nvm.{ Command, Context }

class _orbitup extends Command {


  switches = true
  override def perform(context: Context) {
    world.observer.orbitUp(argEvalDoubleValue(context, 0))
    context.ip = next
  }
}
