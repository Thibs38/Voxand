function generate_xz( x, z )
    freq = 1;
    amp = 1;
    s = 128;
    for _ = 0, 3 do
        s = s + gen.simplex(x,z, 0.01 * freq) * 1 * amp;
        freq = freq * 1;
        amp = amp * 1;
    end
    return
end

function generate_xyz( x, y, z, simplex )
    if y <= simplex - 5 then
        return 3;
    elseif y <= simplex-1 then
        return 2;
    elseif y <= simplex then
        return 1;
    else
        return 0;
    end
end
