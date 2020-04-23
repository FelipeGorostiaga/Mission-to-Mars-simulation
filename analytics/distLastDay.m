function distLastDay(file)
    [date dist] = textread(file,"%s %f");
    
    s = size(dist)(1) - 23;

    d = size(dist)(1) - 0;

    min(dist)
    
    X = 1:23;
    plot(dist(s:d))
    set(gca,'XTick',1:size(dist),'XTickLabel','') 
    hx = get(gca,'XLabel');  % Handle to xlabel 
    set(hx,'Units','data'); 
    pos = get(hx,'Position'); 
    y = pos(2); 
    % Place the new labels 
    for i = 1:23
        t(i) = text(X(i),y,date{i + s}); 
    end 
    set(t,'Rotation',90,'HorizontalAlignment','right')
    ylabel("min distance to Mars (km)")
end
