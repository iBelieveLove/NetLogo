// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.prim

import org.nlogo.core.Syntax
import org.nlogo.nvm.{ Context, Reporter }

class _taskvariable(val varNumber: Int) extends Reporter {

  override def toString =
    super.toString + ":" + varNumber
  override def report(context: Context) =
    // TaskVisitor compiles us out of existence
    throw new IllegalStateException
}
