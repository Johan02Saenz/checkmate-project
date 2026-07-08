package com.saenz.checkmate;

import com.saenz.checkmate.utils.GameValidator;

import org.junit.Test;
import static org.junit.Assert.*;

public class GameValidatorTest {

    // ─── PRUEBA 1 — Validación de ELO ───────────────────────────────

    @Test
    public void validarElo_valorMinimo_retornaTrue() {
        int elo = 400;
        boolean resultado = GameValidator.isValidElo(elo);
        assertTrue("ELO 400 debe ser válido", resultado);
    }

    @Test
    public void validarElo_valorMaximo_retornaTrue() {
        int elo = 2000;
        boolean resultado = GameValidator.isValidElo(elo);
        assertTrue("ELO 2000 debe ser válido", resultado);
    }

    @Test
    public void validarElo_valorFueraDeRango_retornaFalse() {
        int elo = 2001;
        boolean resultado = GameValidator.isValidElo(elo);
        assertFalse("ELO 2001 debe ser inválido", resultado);
    }

    @Test
    public void validarElo_valorNegativo_retornaFalse() {
        // ARRANGE — caso de borde: ELO negativo
        int elo = -100;
        // ACT
        boolean resultado = GameValidator.isValidElo(elo);
        // ASSERT
        assertFalse("ELO negativo debe ser inválido", resultado);
    }

    // ─── PRUEBA 2 — Validación de duración ──────────────────────────

    @Test
    public void validarDuracion_valorPositivo_retornaTrue() {
        // ARRANGE
        long segundos = 342;
        // ACT
        boolean resultado = GameValidator.isValidDuration(segundos);
        // ASSERT
        assertTrue("Una duración mayor a 0 debe ser válida", resultado);
    }

    @Test
    public void validarDuracion_cero_retornaFalse() {
        // ARRANGE — caso de borde: duración exactamente 0
        long segundos = 0;
        // ACT
        boolean resultado = GameValidator.isValidDuration(segundos);
        // ASSERT
        assertFalse("Duración de 0 segundos no debe ser válida", resultado);
    }

    @Test
    public void validarDuracion_negativa_retornaFalse() {
        // ARRANGE — caso de borde: duración negativa
        long segundos = -1;
        // ACT
        boolean resultado = GameValidator.isValidDuration(segundos);
        // ASSERT
        assertFalse("Duración negativa no debe ser válida", resultado);
    }

    // ─── PRUEBA 3 — Validación de resultado ─────────────────────────

    @Test
    public void validarResultado_victory_retornaTrue() {
        // ARRANGE
        String resultado = "VICTORY";
        // ACT
        boolean esValido = GameValidator.isValidResult(resultado);
        // ASSERT
        assertTrue("VICTORY debe ser un resultado válido", esValido);
    }

    @Test
    public void validarResultado_defeat_retornaTrue() {
        assertTrue("DEFEAT debe ser un resultado válido",
                GameValidator.isValidResult("DEFEAT"));
    }

    @Test
    public void validarResultado_draw_retornaTrue() {
        assertTrue("DRAW debe ser un resultado válido",
                GameValidator.isValidResult("DRAW"));
    }

    @Test
    public void validarResultado_invalido_retornaFalse() {
        // ARRANGE — caso de borde: valor no permitido
        String resultado = "ABANDON";
        // ACT
        boolean esValido = GameValidator.isValidResult(resultado);
        // ASSERT
        assertFalse("ABANDON no debe ser un resultado válido", esValido);
    }

    @Test
    public void validarResultado_nulo_retornaFalse() {
        // ARRANGE — caso de borde: null
        assertFalse("null no debe ser un resultado válido",
                GameValidator.isValidResult(null));
    }

    @Test
    public void validarResultado_vacio_retornaFalse() {
        // ARRANGE — caso de borde: cadena vacía
        assertFalse("Cadena vacía no debe ser un resultado válido",
                GameValidator.isValidResult(""));
    }
}