/*
 * Receptor HC-06 -> Arduino
 * Lee los caracteres que envia la app Android via Bluetooth (SPP).
 *
 * Conexiones del HC-06:
 *   VCC -> 5V  (algunos modulos usan 3.3V, comprueba el tuyo)
 *   GND -> GND
 *   TXD del HC-06 -> pin 10 del Arduino (RX por SoftwareSerial)
 *   RXD del HC-06 -> pin 11 del Arduino (TX) a traves de un divisor de
 *                    tension 5V->3.3V (p.ej. R1=1k, R2=2k a GND) para no
 *                    danar el modulo.
 *
 * IMPORTANTE: Desconecta TX/RX del HC-06 al subir el sketch si estuvieras
 * usando los pines 0/1 (Serial por hardware).
 *
 * En la app Android, cada opcion envia el caracter que tu definas
 * (ej. 'a', 'b', 'c', ...). Modifica el switch de loop() para
 * disparar la accion que quieras en cada caso.
 */

#include <SoftwareSerial.h>

// Pines: SoftwareSerial(RX, TX) -> RX del Arduino = pin 10, TX = pin 11
SoftwareSerial BT(10, 11);

const int LED_PIN = 13;   // LED integrado de la mayoria de Arduinos

void setup() {
  Serial.begin(9600);     // Monitor serie (para depurar)
  BT.begin(9600);         // Baudrate por defecto del HC-06
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);
  Serial.println(F("HC-06 listo. Esperando datos..."));
}

void loop() {
  if (BT.available() > 0) {
    char c = BT.read();
    Serial.print(F("Recibido: '"));
    Serial.print(c);
    Serial.println(F("'"));

    switch (c) {
      case 'a':
        // Opcion "Musica 1"
        digitalWrite(LED_PIN, HIGH);
        // TODO: tu accion (reproducir pista 1, mover servo, etc.)
        break;

      case 'b':
        // Opcion "Musica 2"
        digitalWrite(LED_PIN, LOW);
        // TODO: tu accion
        break;

      case 'c':
        // Opcion "Musica 3"
        // TODO: tu accion
        break;

      // Anade aqui los demas casos que necesites...
      // case 'd': ...
      // case 'e': ...

      default:
        // Caracter no reconocido
        break;
    }
  }
}
