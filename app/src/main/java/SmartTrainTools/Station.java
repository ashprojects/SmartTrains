/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;

import java.io.Serializable;
import java.util.Objects;

import jpro.smarttrains.Globals;

/**
 *
 * @author ashish
 */
public class Station implements Serializable {
        private final String code;
        private final String name;

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + Objects.hashCode(this.code);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Station other = (Station) obj;
            if (!Objects.equals(this.code, other.code)) {
                return false;
            }
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return name + " - " + code;
        }

        public Station(String code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public Station(String code){
            this.code=code;
            this.name= Globals.rc.getStationName(code);
        }

        public String getCode() {
            return code;
        }

    }
