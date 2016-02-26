/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.suite.reportyconsult.thread;

import javafx.scene.control.Button;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pe.gob.onpe.suite.common.constant.ConstantReportes;

/**
 *
 * @author kleon
 */
@Component
@Scope("prototype")
public class ReportThread implements Runnable {
    String logg = "[ReportThread : run]";
    Button button;
    Long time = 5000L;
    Long minutos;
    Long segundos;
    boolean isrunning = true;

    @Override
    public void run() {
        while (isIsrunning()) {
            System.out.println(logg+"[INICIO] Se inicio el hilo de Reportes");
            try {
                if (time > ConstantReportes.NUMBER_SESENTA_MIL) {
                    System.out.println(logg+"El hilo se iniciara en "+time / ConstantReportes.NUMBER_SESENTA_MIL + " minutos y " + (time % ConstantReportes.NUMBER_SESENTA_MIL) / ConstantReportes.NUMBER_MIL + " segundos ");
                } else if (time > ConstantReportes.NUMBER_MIL) {
                    System.out.println(logg+"El hilo se iniciara en "+time / ConstantReportes.NUMBER_MIL + " segundos y " + time % ConstantReportes.NUMBER_MIL + " milisegundos ");
                } else {
                    System.out.println(logg+"El hilo se iniciara en "+time + " milisegundos");
                }
                Thread.sleep(getTime());
                System.out.println(logg+"Se inicio el proceso de consulta");
                button.fire();
            } catch (InterruptedException e) {
                System.out.println(logg+"Hilo de Reportes interrumpido");
                isrunning = false;
            }
            System.out.println(logg+"[FIN] Finalizo el hilo de Reportes");
        }
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isIsrunning() {
        return isrunning;
    }

    public void setIsrunning(boolean isrunning) {
        this.isrunning = isrunning;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

}
