function minDistPerDay(file)
    [date dist] = textread(file,"%s %f");
    
    X = 1:size(dist);
    plot(dist)
    set(gca,'XTick',1:size(dist),'XTickLabel','') 
    hx = get(gca,'XLabel');  % Handle to xlabel 
    set(hx,'Units','data'); 
    pos = get(hx,'Position'); 
    y = pos(2); 
    % Place the new labels 
    for i = 1:size(dist) 
        t(i) = text(X(i),y,date{i}); 
    end 
    set(t,'Rotation',90,'HorizontalAlignment','right')
    ylabel("min distance to Mars (km)")
end