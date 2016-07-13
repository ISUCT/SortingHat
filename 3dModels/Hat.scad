height=200;
bottomRadius=200;
topRadius=150;
width=1;
hatField=400;

module innerHat(){
    translate([0, 0, -width]){
            color("red")
            cylinder(h = height-width, r1 = bottomRadius-width, r2 = topRadius-width, center = true);
        }
}

module hat()
{
    difference() {
        color("green")
        translate([0, 0, (-height/2+width/2)])
        cylinder(h = width, r1 = hatField, r2 = hatField, center = true);
        innerHat();
    }
    
    difference() {
        cylinder(h = height, r1 = bottomRadius, r2 = topRadius, center = true);        
        innerHat();
    }
}

hat();