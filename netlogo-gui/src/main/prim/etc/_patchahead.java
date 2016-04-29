// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.prim.etc;

import org.nlogo.api.LogoException;
import org.nlogo.core.Syntax;
import org.nlogo.nvm.Context;
import org.nlogo.nvm.Reporter;

public final strictfp class _patchahead
    extends Reporter {


  @Override
  public Object report(final Context context)
      throws LogoException {
    try {
      org.nlogo.agent.Turtle turtle =
          (org.nlogo.agent.Turtle) context.agent;
      return world.protractor().getPatchAtHeadingAndDistance
          (turtle,
              turtle.heading(),
              argEvalDoubleValue(context, 0));
    } catch (org.nlogo.api.AgentException exc) {
      return org.nlogo.core.Nobody$.MODULE$;
    }
  }
}
