package org.alexdev.kepler.messages.outgoing.games;

import org.alexdev.kepler.game.games.GameParameter;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class GAMEPARAMETERS extends MessageComposer {
    private GameParameter[] parameters;

    public GAMEPARAMETERS(GameParameter[] gameParameters) {
        this.parameters = gameParameters;

        /*

        For snowstorm:

        on setNumberOfTeams(me, tValue)
          tOldElem = "gs_radio_" & pGameParameters.getAt("numTeams") & "teams"
          tNewElem = "gs_radio_" & tValue & "teams"
          pGameParameters.setAt("numTeams", tValue)
          pRenderObj.updateRadioButton("", [tOldElem])
          pRenderObj.updateRadioButton(tNewElem, [])
          return(1)
          exit
        end

        on setGameLength(me, tValue)
          tOldElem = "gs_radio_gamelength_" & pGameParameters.getAt("gameLengthChoice")
          tNewElem = "gs_radio_gamelength_" & tValue
          pGameParameters.setAt("gameLengthChoice", tValue)
          pRenderObj.updateRadioButton("", [tOldElem])
          pRenderObj.updateRadioButton(tNewElem, [])
          return(1)
          exit
        end

        on setFieldType(me, tValue)
          pGameParameters.setAt("fieldType", integer(tValue))
          tWndObj = getWindow(pMainWindowId)
          tDropDown = tWndObj.getElement("gs_dropmenu_gamefield")
          if not ilk(tDropDown, #instance) then
            return(error(me, "Unable to retrieve dropdown:" && tDropDown, #setFieldType))
          end if
          tFieldTxtItems = []
          tFieldKeyItems = []
          i = 1
          repeat while i <= 7
            tFieldTxtItems.setAt(i, getText("sw_fieldname_" & i))
            tFieldKeyItems.setAt(i, string(i))
            i = 1 + i
          end repeat
          tDropDown.updateData(tFieldTxtItems, tFieldKeyItems, void(), tValue)
          return(1)
          exit
end

         */
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.parameters.length);

        for (GameParameter parameter : this.parameters) {
            response.writeString(parameter.getName());
            response.writeBool(!parameter.hasMinMax());
            response.writeInt(parameter.isEditable() ? 2 : 0);

            if (parameter.hasMinMax()) {
                response.writeInt(Integer.parseInt(parameter.getDefaultValue()));

                if (parameter.getMin() != -1) {
                    response.writeBool(true);
                    response.writeInt(parameter.getMin());
                }

                if (parameter.getMax() != -1) {
                    response.writeBool(true);
                    response.writeInt(parameter.getMax());
                }

            } else {
                response.writeString(parameter.getDefaultValue());
                response.writeInt(0);
            }
        }
    }

    @Override
    public short getHeader() {
        return 235; // "Ck"
    }
}
